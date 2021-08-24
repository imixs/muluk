FROM payara/micro:latest
#FROM payara/micro:5.194

MAINTAINER ralph.soika@imixs.com
# Deploy artefacts
COPY ./target/*.war $DEPLOY_DIR

