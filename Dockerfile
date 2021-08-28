FROM jboss/wildfly:22.0.1.Final

LABEL description="Imixs-Muluk"
LABEL maintainer="ralph.soika@imixs.com"


# Setup configuration
COPY ./docker/configuration/wildfly/* /opt/jboss/wildfly/standalone/configuration/

# Deploy artefact
ADD ./target/*.war /opt/jboss/wildfly/standalone/deployments/
