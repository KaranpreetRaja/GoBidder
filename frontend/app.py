from flask import Flask, render_template, request, redirect, url_for, flash, session
from datetime import datetime, timezone, timedelta
from functools import wraps
import json
from urllib.parse import urljoin
import requests
import os

app = Flask(__name__)
app.secret_key = os.urandom(24).hex()

AUTH_API = os.getenv("AUTH_API", "http://auth-service:8081")
AUCTION_API = os.getenv("AUCTION_API", "http://auction-service:8083")
PAYMENT_API = os.getenv("PAYMENT_API", "http://payment-service:8082")
BID_SERVICE = os.getenv("BID_SERVICE", "http://bid-service:8084")
NOTIFICATION_WS = os.getenv("NOTIFICATION_WS", "ws://notification-service:8085")

def login_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        if "token" not in session:
            return redirect(url_for("login"))
        return f(*args, **kwargs)

    return decorated_function


def fetch_user_profile(token):
    try:
        response = requests.get(
            f"{AUTH_API}/users/me", headers={"Authorization": f"Bearer {token}"}
        )
        if response.ok:
            user_data = response.json()
            session["user_id"] = user_data["id"]
            return True
        return False
    except requests.exceptions.RequestException:
        return False


@app.route("/")
def index():
    return redirect(url_for("auctions"))


@app.route("/login", methods=["GET", "POST"])
def login():
    if request.method == "POST":
        try:
            response = requests.post(
                f"{AUTH_API}/auth/login",
                json={
                    "email": request.form["email"],
                    "password": request.form["password"],
                },
            )
            if response.ok:
                data = response.json()
                session["token"] = data["token"]
                if fetch_user_profile(data["token"]):
                    return redirect(url_for("auctions"))
                flash("Failed to fetch user profile", "danger")

            else:
                flash("Invalid credentials", "danger")

        except requests.exceptions.RequestException:
            flash("Service unavailable", "danger")

    return render_template("login.html")


@app.route("/logout")
def logout():
    session.clear()
    flash("You have been logged out successfully", "success")
    return redirect(url_for("login"))


@app.route("/signup", methods=["GET", "POST"])
def signup():
    if request.method == "POST":
        try:
            response = requests.post(
                f"{AUTH_API}/auth/signup",
                json={
                    "email": request.form["email"],
                    "password": request.form["password"],
                    "fullName": request.form["fullName"],
                    "cardNumber": request.form["cardNumber"],
                    "csv": request.form["csv"],
                    "expirationDate": request.form["expirationDate"],
                    "billingAddress": request.form["billingAddress"],
                },
            )

            if response.ok:
                # If user creation is successful, attempt to log them in
                login_response = requests.post(
                    f"{AUTH_API}/auth/login",
                    json={
                        "email": request.form["email"],
                        "password": request.form["password"]
                    }
                )
                
                if login_response.ok:
                    data = login_response.json()
                    session["token"] = data["token"]
                    if fetch_user_profile(data["token"]):
                        flash("Account created successfully", "success")
                        return redirect(url_for("auctions"))
                    
                flash("Account created but login failed", "warning")
                return redirect(url_for("login"))
            else:
                error_msg = response.json().get("message", "Signup failed")
                flash(f"Signup failed: {error_msg}", "danger")
        except requests.exceptions.RequestException as e:
            flash(f"Service unavailable: {str(e)}", "danger")
        except KeyError as e:
            flash(f"Missing required field: {str(e)}", "danger")
    return render_template("signup.html")


@app.route("/auctions")
@login_required
def auctions():
    try:
        response = requests.get(f"{AUCTION_API}/auction")
        auctions = response.json() if response.ok else []
        return render_template("auctions.html", auctions=auctions)
    except requests.exceptions.RequestException:
        flash("Failed to fetch auctions", "danger")
        return render_template("auctions.html", auctions=[])


@app.route("/auction/create", methods=["GET", "POST"])
@login_required
def create_auction():
    if request.method == "POST":
        try:
            start_time = datetime.fromisoformat(request.form["startTime"])
            current_time = datetime.now()

            if start_time <= current_time - timedelta(days=1):
                flash("Auction start time must be in the future", "danger")
                return render_template("create_auction.html")

            data = {
                "name": request.form["name"],
                "description": request.form["description"],
                "type": request.form["type"],
                "auctionOwnerId": session.get("user_id"),
                "initialPrice": float(request.form["initialPrice"]),
                "startTime": request.form["startTime"],
                "minimumPrice": float(request.form["minimumPrice"]),
                "duration": int(request.form["duration"]),
                "auctionImageUrl": request.form["auctionImageUrl"]
            }
            response = requests.post(f"{AUCTION_API}/auction", json=data)
            if response.ok:
                flash("Auction created successfully", "success")
                return redirect(url_for("auctions"))
            flash("Failed to create auction", "danger")
        except requests.exceptions.RequestException:
            flash("Service unavailable", "danger")
        except ValueError:
            flash("Invalid date/time format", "danger")
    return render_template("create_auction.html")


@app.route("/auction/<int:id>")
@login_required
def view_auction(id):
    try:
        response = requests.get(f"{AUCTION_API}/auction/{id}")
        if response.ok:
            auction = response.json()
            
            # If auction is won, create highest_bid object from auction data
            if auction.get('status') == 'WON' and auction.get('highestBidderId'):
                highest_bid = {
                    'userId': auction['highestBidderId'],
                    'bidderPrice': auction['currentPrice']
                }
            else:
                # For non-won auctions, get latest bid info
                bid_response = requests.get(
                    f"{AUCTION_API}/auction/{id}/bid",
                    headers={"Authorization": f"Bearer {session.get('token')}"}
                )
                highest_bid = bid_response.json() if bid_response.ok else None
                
            return render_template(
                "auction_detail.html", 
                auction=auction, 
                highest_bid=highest_bid
            )
        flash("Auction not found", "danger")
    except requests.exceptions.RequestException:
        flash("Service unavailable", "danger")
    return redirect(url_for("auctions"))


@app.route("/auction/<int:id>/start", methods=["POST"])
@login_required
def start_auction(id):
    try:
        response = requests.post(f"{AUCTION_API}/auction/{id}/start")
        if response.ok:
            flash("Auction started successfully", "success")
        else:
            flash("Failed to start auction", "danger")
    except requests.exceptions.RequestException:
        flash("Service unavailable", "danger")
    return redirect(url_for("view_auction", id=id))



# Update the place_bid route in app.py
@app.route("/auction/<int:id>/bid", methods=["POST"])
@login_required
def place_bid(id):
    try:
        bid_amount = float(request.form["bid"])
        
        # Use bid service instead of auction service
        response = requests.post(
            f"{BID_SERVICE}/api/bids/placeBid",
            json={
                "auctionId": str(id),
                "price": bid_amount,
                "userId": str(session.get("user_id"))
            },
            headers={"Authorization": f"Bearer {session.get('token')}"}  # pass the auth token
        )
        
        if response.ok:
            data = response.json()
            if data.get("status") == "SUCCESS":
                flash("Bid placed successfully", "success")
            else:
                flash(data.get("message", "Failed to place bid"), "danger")
        else:
            flash("Failed to place bid", "danger")
    except requests.exceptions.RequestException:
        flash("Service unavailable", "danger")
    return redirect(url_for("view_auction", id=id))

# Add a new route to get WebSocket URL
@app.route("/ws-config")
@login_required
def ws_config():
    return {"wsUrl": f"{NOTIFICATION_WS}/ws/subscribe"}

@app.route("/auction/<int:id>/purchase", methods=["POST"])
@login_required
def purchase_auction(id):
    try:
        response = requests.get(f"{AUCTION_API}/auction/{id}")
        if not response.ok:
            flash("Could not verify auction details", "danger")
            return redirect(url_for("view_auction", id=id))
            
        auction = response.json()
        if auction["status"] != "WON" or auction["highestBidderId"] != session.get("user_id"):
            flash("You are not the winner of this auction", "danger")
            return redirect(url_for("view_auction", id=id))

        payment_response = requests.post(
            f"{PAYMENT_API}/api/payment/validate/jwt",
            json={"token": session.get("token")},
        )

        if payment_response.ok and payment_response.json().get("success"):
            # Create receipt data
            receipt_data = {
                "auction": auction,
                "bidderPrice": auction["currentPrice"]
            }
            return render_template("receipt.html", bid=receipt_data)
        else:
            session["payment_error"] = payment_response.json().get("message", "Payment failed")
            return redirect(url_for("payment_failure"))

    except requests.exceptions.RequestException:
        flash("Service unavailable", "danger")
        return redirect(url_for("view_auction", id=id))


@app.route("/payment/failure")
def payment_failure():
    return render_template("payment_failure.html", error=session.get("payment_error"))


@app.route("/auction/<int:id>/receipt")
@login_required
def auction_receipt(id):
    try:
        bid_response = requests.get(
            f"{AUCTION_API}/auction/{id}/bid",
            headers={"Authorization": f'Bearer {session.get("token")}'},
        )
        if bid_response.ok:
            bid_data = bid_response.json()
            requests.delete(f"{AUCTION_API}/auction/{id}")
            return render_template("receipt.html", bid=bid_data)
    except requests.exceptions.RequestException:
        flash("Error retrieving receipt", "danger")
    return redirect(url_for("auctions"))


if __name__ == "__main__":
    app.run(debug=True, host="0.0.0.0", port=3000)
