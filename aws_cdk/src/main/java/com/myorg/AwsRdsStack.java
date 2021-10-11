package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.kms.IKey;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.secretsmanager.ISecret;

import java.util.Collections;

public class AwsRdsStack extends Stack {
    public AwsRdsStack(final Construct scope, final String id, Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public AwsRdsStack(final Construct scope, final String id, final StackProps props, Vpc vpc) {
        super(scope, id, props);

        // The code that defines your stack goes here
        CfnParameter databasePass = CfnParameter.Builder.create(this,"dbPassword")
                .type("String")
                .description("The RDS instance password")
                .build();

        ISecurityGroup iSecurityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        iSecurityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(3306));

        DatabaseInstance databaseInstance = DatabaseInstance.Builder
                .create(this, "Rds01")
                .instanceIdentifier("aws-project01-db")
                .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                                .version(MysqlEngineVersion.VER_5_7_34)
                        .build()))
                .vpc(vpc)
                .credentials(Credentials.fromPassword("admin", SecretValue.plainText(databasePass.getValueAsString())))

                        /*Credentials.fromUsername("admin", CredentialsFromUsernameOptions.builder()
                        .password(SecretValue.plainText(databasePass.getValueAsString()))*/
                        //.build()//))
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .multiAz(false)
                .allocatedStorage(10)
                .securityGroups(Collections.singletonList(iSecurityGroup))
                .vpcSubnets(SubnetSelection.builder()
                        .subnets(vpc.getPrivateSubnets())
                        .build())
                .build();

        CfnOutput.Builder.create(this, "rds-endpoint")
                .exportName("rds-endpoint")
                .value(databaseInstance.getDbInstanceEndpointAddress())
                .build();

        CfnOutput.Builder.create(this, "rds-password")
                .exportName("rds-password")
                .value(databasePass.getValueAsString())
                .build();
    }
}
