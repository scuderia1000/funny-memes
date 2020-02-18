# funny-memes
Service for downloading data from reddit group, parsing content, uploading image files to aws s3 and save data to MongoDB Atlas.

For configuration, add env variables:

```json
SPRING_DATA_MONGODB_URI:
  
  For local MongoDB
  mongodb://localhost:27017/test
  
  For cloud MongoDB (java mongodb-driver 3.6 or later)
  mongodb+srv://<user_name>:<password>@<cluster>.mongodb.net/test?retryWrites=true&w=majority<br/>
 
WS_ACCESS_KEY_ID=access_key<br/>
AWS_SECRET_ACCESS_KEY=secret<br/>
AWS_REGION=region<br/>
```
For docker, create file env_file_name.env with this env variables (KEY=VALUE) and run docker with command:
```json
$ docker build --rm -t funny/memes .

$ docker run -p 8080:8080 --env-file ./env_file_name.env funny/memes
```
Add bucket policy:
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

