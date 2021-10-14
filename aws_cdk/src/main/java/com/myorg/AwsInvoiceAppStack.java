package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.RemovalPolicy;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.notifications.SnsDestination;
import software.amazon.awscdk.services.s3.notifications.SqsDestination;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.SqsSubscription;
import software.amazon.awscdk.services.sqs.DeadLetterQueue;
import software.amazon.awscdk.services.sqs.Queue;

public class AwsInvoiceAppStack extends Stack {
    private final Bucket bucket;
    private final Queue queue;

    public AwsInvoiceAppStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsInvoiceAppStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here
        SnsTopic snsTopic = SnsTopic.Builder.create(Topic.Builder.create(this, "S3InvoiceTopic")
                .topicName("s3-invoice-events")
                .build())
                .build();

        bucket = Bucket.Builder.create(this, "S301")
                .bucketName("pcs01-invoice")
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        bucket.addEventNotification(EventType.OBJECT_CREATED_PUT, new SnsDestination(snsTopic.getTopic()));

        Queue s3InvoiceDlq = Queue.Builder.create(this, "s3InvoiceDlq")
                .queueName("s3-invoice-events-dlq")
                .build();

        DeadLetterQueue deadLetterQueue = DeadLetterQueue.builder()
                .queue(s3InvoiceDlq)
                .maxReceiveCount(3)
                .build();

        queue = Queue.Builder.create(this, "s3InvoiceQueue")
                .queueName("s3-invoice-events")
                .deadLetterQueue(deadLetterQueue)
                .build();

        SqsSubscription sqsSubscription = SqsSubscription.Builder.create(queue).build();
        snsTopic.getTopic().addSubscription(sqsSubscription);

    }

    public Bucket getBucket() {
        return bucket;
    }

    public Queue getQueue() {
        return queue;
    }
}
