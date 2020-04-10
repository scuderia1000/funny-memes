# funny-memes
An Spring Boot application for downloading data from Reddit groups, parsing content (only jpeg images), uploading image files to AWS S3 and save data to MongoDB Atlas Cloud.

Demo app: https://funmem.herokuapp.com/

For configuration, add env variables:

```json
SPRING_DATA_MONGODB_URI:
  
  For local MongoDB
  mongodb://localhost:27017/test
  
  For cloud MongoDB (java mongodb-driver 3.6 or later)
  mongodb+srv://<user_name>:<password>@<cluster>.mongodb.net/test?retryWrites=true&w=majority<br/>
 
WS_ACCESS_KEY_ID=access_key
AWS_SECRET_ACCESS_KEY=secret
AWS_REGION=region
```
For docker, create file env_file_name.env with this env variables (KEY=VALUE) and run docker with command:
```json
$ docker build --rm -t funny-memes .

$ docker run -p 8080:8080 --env-file ./env_file_name.env funny-memes
```
Add bucket policy to AWS S3:
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Principal": "*",
            "Action": "s3:GetObject",
            "Resource": "arn:aws:s3:::<bucket-name>/*"
        }
    ]
}
```
In application.properties file change:<br/>
```json
app.awsServices.bucketName=<your_bucket_name>
```
In application.yml file
```json
reddit:
  groups:
    <group lang>:
      - <add your own groups>
```

For Heroku deployments, create account on Heroku, install Heroku CLI and do following commands in app root directory:
```json
$ heroku login
$ heroku container:login
$ heroku create <artifactId from pom file>
$ heroku container:push web
$ heroku container:release web
$ heroku open
```