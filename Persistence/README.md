# Standalone usage

1. Build the database image inside the _/persistence/database_ folder: `docker build -t database .`
2. Build the persistence image inside the _/persistence_ folder: `docker build -t persistence .`
3. Run the database container via: `docker run --name database -p 5432:5432 -d database`
4. After this, run the persistence container via: `docker run --name persistence -p 2002:2002 --link database:postgres -d persistence`
5. You should now be able to access the Spring Boot application via `localhost:2002/` plus the REST-Endpoint

