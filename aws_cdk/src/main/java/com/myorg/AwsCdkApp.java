package com.myorg;

import software.amazon.awscdk.core.App;

public class AwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        //vpc é criado primeiro, devido a dependencia do cluster
        AwsVpcStack awsVpcStack = new AwsVpcStack(app, "Vpc");

        AwsClusterStack awsClusterStack = new AwsClusterStack(app,"Cluster", awsVpcStack.getVpc());
        awsClusterStack.addDependency(awsVpcStack);

        AwsRdsStack awsRdsStack = new AwsRdsStack(app, "Rds", awsVpcStack.getVpc());
        awsRdsStack.addDependency(awsVpcStack);

        AwsSnsStack awsSnsStack = new AwsSnsStack(app,"Sns");

        AwsService01Stack awsService01Stack = new AwsService01Stack(app, "Service01", awsClusterStack.getCluster(), awsSnsStack.getSnsTopic());
        awsService01Stack.addDependency(awsClusterStack);
        awsService01Stack.addDependency(awsRdsStack);
        awsService01Stack.addDependency(awsSnsStack);

        AwsDynamoStack awsDynamoStack = new AwsDynamoStack(app, "Dynamo01");

        AwsService02Stack awsService02Stack = new AwsService02Stack(app, "Service02", awsClusterStack.getCluster(),
                awsSnsStack.getSnsTopic(), awsDynamoStack.getProductEventsDynamo());
        awsService02Stack.addDependency(awsClusterStack);
        awsService02Stack.addDependency(awsSnsStack);
        awsService02Stack.addDependency(awsDynamoStack);





        app.synth();
    }
}
