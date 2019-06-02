#!/bin/bash

echo
echo '--- Build SOFA-Dashboard Docker image ---'
echo "  DOCKER_BUILD_DIR=${DOCKER_BUILD_DIR}"
echo "  DOCKER_IMG_NAME=${DOCKER_IMG_NAME}"
echo "  SCRIPTS_DIR=${SCRIPTS_DIR}"
echo "  ARTIFACT_JAR=${ARTIFACT_JAR}"
echo

# Re-create build context
rm -rf ${DOCKER_BUILD_DIR} && mkdir -p ${DOCKER_BUILD_DIR}

# Copy artifact resources
cp -r ${SCRIPTS_DIR}/*  ${DOCKER_BUILD_DIR}
cp ${ARTIFACT_JAR}      ${DOCKER_BUILD_DIR}/sofa-dashboard-web-latest.jar


docker build ${DOCKER_BUILD_DIR} -t ${DOCKER_IMG_NAME}
