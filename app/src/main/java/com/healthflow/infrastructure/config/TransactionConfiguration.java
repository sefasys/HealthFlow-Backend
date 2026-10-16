package com.healthflow.infrastructure.config;


import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@Profile("jpa")   // in-memory profilde TransactionManager yok, bu config yüklenmemeli
public class TransactionConfiguration {

    @Bean
    public Advisor applicationServiceTransactionAdvisor(TransactionManager transactionManager) {

        RuleBasedTransactionAttribute readOnly = new RuleBasedTransactionAttribute();
        readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        readOnly.setReadOnly(true);
        readOnly.setTimeout(10);          // saniye: "uzun/belirsiz transaction" olmasın

        RuleBasedTransactionAttribute write = new RuleBasedTransactionAttribute();
        write.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        write.setTimeout(10);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.addTransactionalMethod("get*", readOnly);
        source.addTransactionalMethod("find*", readOnly);
        source.addTransactionalMethod("search*", readOnly);
        source.addTransactionalMethod("*", write);   // create*, update*, book*, cancel*...

        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(
                "execution(public * com.healthflow.application.service..*Service.*(..))");

        return new DefaultPointcutAdvisor(
                pointcut, new TransactionInterceptor(transactionManager, source));
    }
}