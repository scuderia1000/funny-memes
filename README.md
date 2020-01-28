# funny-memes
Service for downloading data from reddit group, parsing content, uploading image files to aws s3 and save data to MongoDB Atlas.

For configuration, add env variables:

SPRING_DATA_MONGODB_URI: 
  For local MongoDB
  mongodb://localhost:27017/test
  For cloud MongoDB (java mongodb-driver 3.6 or later)
  mongodb+srv://<user_name>:<password>@<cluster>.mongodb.net/test?retryWrites=true&w=majority
  
WS_ACCESS_KEY_ID=access_key
AWS_SECRET_ACCESS_KEY=secret
AWS_REGION=region

Add bucket policy:
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

In application.properties file change:
  app.awsServices.bucketName=<your bucket name>
  
  In reddit.group you can add your own groups
  
