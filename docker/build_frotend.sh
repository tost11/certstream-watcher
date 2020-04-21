docker run -it --rm --name my-maven-project-certchecker \
  -v $(pwd)/../frontend:/proj \
  -w /proj \
  node:8.16	 \
  /bin/bash -c "npm install && npm run build"
