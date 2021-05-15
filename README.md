# Memory

My take on the classics Memory game

The goal is to return pairs of images, those that are the same stays, and those that aren't hides again

## How to compile and run (Linux)

1. Install maven

```shell
sudo apt-get install maven
```

2. Navigate to the repository

```shell
cd PATH/TO/REPOSITORY
```

3. Clean and Compile
```shell
mvn clean
mvn install
```

4. Navigate to target folder

```shell
cd target
```

5. Add the "execute" permission to Memory-X.x.jar (replace X-x by the version)

```shell
chmod a+x Memory-X.x.jar
```

6. Run the .jar (replace X-x by the version)

```shell
java -jar Memory-X.x.jar
```

(You can also double-click on the .jar once it has the "execute" permission)