# funny-memes
Service for downloading data from reddit group, parsing content, uploading image files to aws s3 and save data to MongoDB Atlas.

For configuration, add env variables:

SPRING_DATA_MONGODB_URI:<br/>
  For local MongoDB<br/>
  mongodb://localhost:27017/test<br/>
  For cloud MongoDB (java mongodb-driver 3.6 or later)<br/>
  mongodb+srv://<user_name>:<password>@<cluster>.mongodb.net/test?retryWrites=true&w=majority<br/>
 
WS_ACCESS_KEY_ID=access_key<br/>
AWS_SECRET_ACCESS_KEY=secret<br/>
AWS_REGION=region<br/>

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
  
reddit.group= #you_can_add_your_own_groups
```
