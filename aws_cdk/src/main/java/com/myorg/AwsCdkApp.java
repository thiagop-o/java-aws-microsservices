package com.myorg;

import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.Environment;
import software.amazon.awscdk.core.StackProps;

import java.util.Arrays;

public class AwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        //vpc é criado primeiro, devido a dependencia do cluster
        AwsVpcStack awsVpcStack = new AwsVpcStack(app, "Vpc");

        AwsClusterStack awsClusterStack = new AwsClusterStack(app,"Cluster", awsVpcStack.getVpc());
        awsClusterStack.addDependency(awsVpcStack);

        AwsRdsStack awsRdsStack = new AwsRdsStack(app, "Rds", awsVpcStack.getVpc());
        awsRdsStack.addDependency(awsVpcStack);

        AwsService01Stack awsService01Stack = new AwsService01Stack(app, "Service01", awsClusterStack.getCluster());
        awsService01Stack.addDependency(awsClusterStack);
        awsService01Stack.addDependency(awsRdsStack);



        app.synth();
    }
}
