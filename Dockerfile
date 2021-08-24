FROM jboss/wildfly:20.0.1.Final

LABEL description="Imixs-Muluk"
LABEL maintainer="ralph.soika@imixs.com"


# Deploy artefact
ADD ./target/*.war /opt/jboss/wildfly/standalone/deployments/
