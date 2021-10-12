package com.myorg;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.EmailSubscription;

public class AwsSnsStack extends Stack {
    private final SnsTopic snsTopic;
    public AwsSnsStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsSnsStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here
        snsTopic = SnsTopic.Builder.create(Topic.Builder.create(this, "ProductEventsTopic")
                .topicName("product-events")
                .build())
                .build();

        snsTopic.getTopic().addSubscription(EmailSubscription.Builder.create("thiagop_o@hotmail.com")
                .json(true)
                .build());
    }

    public SnsTopic getSnsTopic() {
        return snsTopic;
    }
}
