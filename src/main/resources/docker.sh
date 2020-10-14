docker build -t ${jarName}:latest . -f Dockerfile &&
docker run -d -it -v /usr/configDir/:/data ${jarName}:latest /bin/bash &&