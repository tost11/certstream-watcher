docker run -it --rm --name my-maven-project-certchecker \
  -v $(pwd)/../backend:/usr/src/mymaven \
  -w /usr/src/mymaven \
  maven:3.6-jdk-11 \
  mvn clean install
