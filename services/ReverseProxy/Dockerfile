# Use nginx image
FROM nginx:alpine

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Expose ports
EXPOSE 80
EXPOSE 8080

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
