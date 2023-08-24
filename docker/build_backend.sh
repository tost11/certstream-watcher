docker run -it --rm --name my-maven-project-certchecker \
  -v $(pwd)/../backend:/backend \
  -v $(pwd)/../certstream-java:/certstream-java \
  maven:3.6-jdk-11 \
  bash -c "cd /certstream-java &&  mvn clean install && cd /backend && mvn clean install"