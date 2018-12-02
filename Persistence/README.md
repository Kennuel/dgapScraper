Standalone usage

1. Build the image inside the /persistence folder: `docker build -t persistence .`
2. Run the container via: `docker run --name persistence -p 2002:2002 -d persistence`
3. You should now be able to access the Spring Boot application via `localhost:2002/` plus the REST-Endpoint