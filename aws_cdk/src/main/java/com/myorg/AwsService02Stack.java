package com.myorg;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;

import java.util.HashMap;
import java.util.Map;


public class AwsService02Stack extends Stack {
    public AwsService02Stack(final Construct scope, final String id, Cluster cluster, SnsTopic snsTopic, Table productEventsDbd) {
        this(scope, id, null, cluster, snsTopic, productEventsDbd);
    }

    public AwsService02Stack(final Construct scope, final String id, final StackProps props, Cluster cluster, SnsTopic snsTopic, Table productEventsDbd) {
        super(scope, id, props);

        Queue productEventsDql = Queue.Builder.create(this, "ProductEventsDql")
                .queueName("product-events-dql")
                .build();

        DeadLetterQueue deadLetterQueue = DeadLetterQueue.builder()
                .queue(productEventsDql)
                .maxReceiveCount(3)
                .build();

        Queue productEventsQueue = Queue.Builder.create(this, "ProductEvents")
                .queueName("product-events")
                .deadLetterQueue(deadLetterQueue)
                .build();

        SqsSubscription sqsSubscription = SqsSubscription.Builder.create(productEventsQueue).build();
        snsTopic.getTopic().addSubscription(sqsSubscription);

        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("AWS_REGION", "sa-east-1");
        envVariables.put("AWS_SQS_QUEUE_PRODUCT_EVENTS_NAME", productEventsQueue.getQueueName());


        // The code that defines your stack goes here
        ApplicationLoadBalancedFargateService service02 = ApplicationLoadBalancedFargateService.Builder.create(this,"ALB02")
                .serviceName("service-02")
                .cluster(cluster)
                .cpu(512)
                .memoryLimitMiB(1024)
                .desiredCount(1)
                .listenerPort(9090)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .containerName("java-aws-microsservices02")
                                .image(ContainerImage.fromRegistry("thiagopo/java-aws-microsservices02:2.0.0"))
                                .containerPort(9090)
                                .logDriver(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                        .logGroup(LogGroup.Builder.create(this,"Service02LogGroup")
                                                .logGroupName("Service02")
                                                .removalPolicy(RemovalPolicy.DESTROY)
                                                .build())
                                        .streamPrefix("Service02")
                                        .build()))
                                .environment(envVariables)
                                .build())
                .publicLoadBalancer(true)
                .build();

        service02.getTargetGroup().configureHealthCheck(new HealthCheck.Builder()
                .path("/actuator/health")
                .port("9090")
                .healthyHttpCodes("200")
                .build());

        ScalableTaskCount scalableTaskCount = service02.getService().autoScaleTaskCount(EnableScalingProps.builder()
                .minCapacity(1)
                .maxCapacity(2)
                .build());

        scalableTaskCount.scaleOnCpuUtilization("Service02AutoScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50)
                .scaleInCooldown(Duration.seconds(60))
                .scaleOutCooldown(Duration.seconds(60))
                .build());

        productEventsQueue.grantConsumeMessages(service02.getTaskDefinition().getTaskRole());
        productEventsDbd.grantReadWriteData(service02.getTaskDefinition().getTaskRole());


    }
}
