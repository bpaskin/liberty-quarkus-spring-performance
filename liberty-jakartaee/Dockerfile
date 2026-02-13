FROM icr.io/appcafe/open-liberty:full-java21-openj9-ubi

# Copy PostgreSQL driver
COPY --chown=1001:0 target/liberty-jakartaee.war /config/apps/

# Copy server configuration
COPY --chown=1001:0 src/main/liberty/config/server.xml /config/

# Create directory for PostgreSQL driver
USER root
RUN mkdir -p /opt/ol/wlp/usr/shared/resources/postgresql && \
    chown -R 1001:0 /opt/ol/wlp/usr/shared/resources/postgresql
USER 1001

# Download PostgreSQL JDBC driver
ADD --chown=1001:0 https://jdbc.postgresql.org/download/postgresql-42.7.3.jar /opt/ol/wlp/usr/shared/resources/postgresql/

# Configure Liberty
RUN configure.sh