import axios, { AxiosInstance } from 'axios';

// Base URLs for different microservices
const AUTH_API_BASE_URL = process.env.NEXT_PUBLIC_AUTH_API_BASE_URL;
const PAYMENT_API_BASE_URL = process.env.NEXT_PUBLIC_PAYMENT_API_BASE_URL;
const AUCTION_API_BASE_URL = process.env.NEXT_PUBLIC_AUCTION_API_BASE_URL;

/**
 * Axios instance for requests to the auth microservice.
 */
const authAxios = axios.create({ baseURL: AUTH_API_BASE_URL });

/**
 * Axios instance for requests to the auction microservice.
 */
const auctionAxios = axios.create({ baseURL: AUCTION_API_BASE_URL });

/**
 * Axios instance for requests to the payment microservice.
 */
const paymentAxios = axios.create({ baseURL: PAYMENT_API_BASE_URL });

/**
 * Adds request and response interceptors to an axios instance.
 *
 * @param axiosInstance The axios instance to adds the interceptors to.
 */
const addInterceptors = (axiosInstance: AxiosInstance) => {
    /*
     * Request interceptor.
     */
    axiosInstance.interceptors.request.use(
        (config) => {
            console.log(`Intercepting request to ${config.url}`);

            // Exclude certain routes (e.g., login and signup) from
            // authorization
            const excludedRoutes = ['/login', '/signup'];
            if (excludedRoutes.some((route) => config.url?.includes(route))) {
                // Return here without adding auth header
                console.log("Excluded route - skipping interception");
                return config;
            }

            // Get and validate token from local storage
            const token = localStorage.getItem('jwt');
            if (!token) {
                console.error('No JWT, redirecting to login');
                window.location.href = '/login';
            }

            // Attach JWT to auth header
            console.log("JWT attached to request, forwarding...");
            config.headers.Authorization = `Bearer ${token}`;
            return config;
        },
        (error) => Promise.reject(error)
    );

    /*
     * Response interceptor.
     */
    axiosInstance.interceptors.response.use(
        (response) => response,
        (error) => {
            console.log(`Intercepting response error from ${error}`);
            if (error.response?.status === 401) {
                console.error('Unauthorized - Redirecting to login');
                // window.location.href = '/login';
            }
            return Promise.reject(error);
        }
    );
};

// Apply interceptors to all instances
addInterceptors(authAxios)
addInterceptors(auctionAxios);
addInterceptors(paymentAxios);

export { authAxios, paymentAxios, auctionAxios };
