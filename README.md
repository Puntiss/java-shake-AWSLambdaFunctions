# Description
Made using spring and deployed on AWS infostructure this is the back-end that contains multiple beans and services to:
1. Make CRUD operation on the *shake products*.
2. Handle the register and login phase for *users* creating a [JWT Token](https://jwt.io/)
   - The password is RSA256 encrypted.
   - The session is limited in time, every while you have to re-login for better security.
   - The cart persistence is not handled so the front-end should manage it.
3. Manage the creation of an *order* made by a registered user using an e-commerce cart that contains multiple products
   - When an order is created, the numOrder value is updated.
   - When an order is made the qtyAvailable is not updated to avoid that this demo project should be refilled every time. If implemented this feature will impact the cart component because if the user adds 10 pz of an item in his cart and another client buys all the pz available the order will be made anyway but in the stock the items are not present.
  
Every request is handled with a JSON input that contains the required params, and the response is always returned as an object with Code, Message and Data. All the details are in the decoration of the specific methods. 

This Java project when built with *maven* produces a Jar that contains all the functions and to call the desired one you have to set the variable `spring_cloud_function_definition` with the name of the beans that you want.

All the functions are designed to be deployed on AWS Lambda infostructure and to be called by an AWS Gateway endpoint. The data are designed to be stored in a DynamoDB database. The project is provided with a *template.yml* file for the AWS Serverless Application Model (AWS SAM) that will create and configure all the resources needed.

If you want to create a web application that will be "executed" by the modern browser you will need a CORS configuration, provided in the file *api.yml*
# Usage and Modify
**0. Prerequisites:**

- Install [SAM  1.97.0+](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html) or check if already installed with `sam --version`.
- Install [AWS CLI 2.13.20+](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html)  or check if already installed with `aws --version`.
- Install [Java SE JDK-17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or check if already installed with `java -version`.
- Install [Maven 3.8.4+](https://maven.apache.org/install.html) or check if already installed with `mvn -v`.

**1. Configure AWS User**

- Use the AWS IAM console to create a user with the AdministatorAccess policy and configure it into aws cli with `aws configure` and insert as parameters *access key*, *secret* (user > security credentials > access key), your [region](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Concepts.RegionsAndAvailabilityZones.html) and *json*. (You can test if all data are correctly configured with `aws iam get-user`)
- Use the AWS IAM console to create another user with the AmazonDynamoDBFullAccess policy and obtain the *access key* and *secret* of this user (user > security credentials > access key).  
> [!NOTE]
> Is safer to use two different users.

**2. Configure Keys**

- Configure the *com\appa\serverless\service\DynamoDBService.java* `key` and `secret` with the credentials previously created.
- Configure the *com\appa\serverless\service\EncryptService.java* `PUBLIC_KEY` and `PRIVATE_KEY` with the credentials you want.
- Configure the *com\appa\serverless\service\JWTService.java* `SECRET_KEY` with the credentials you want.
    
**3. Build**

- Install all project dependencies specified in the pom.xml file using `mvn clean install`, a target folder will be created.
- In the *template.yml* edit the CodeUri with the *.jar* file path.

**4. Deploy**

- Create an AWS S3 Buket using the user interface or with `aws s3 mb s3://NAME_OF_THE_BUCKET`.
- Configure the *template.yml* `--s3-bucket` with the name of the bucket created.
- Configure the *template.yml* `--stack-name` with the name of the stak you want.
- In the same folder of the template.yml run `sam deploy --s3-bucket NAME_OF_THE_BUCKET_CREATED --stack-name CHOOSE_STACK_NAME --capabilities CAPABILITY_IAM`.

 **5. Configure CORS**
- To apply CORS configuration 




   
**5. Testing**







Happy coding!
