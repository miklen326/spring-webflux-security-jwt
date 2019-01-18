package london.secondscreen.livehub.security.service;

import london.secondscreen.livehub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.concurrent.Callable;

@Service
public class UserDetailsService implements ReactiveUserDetailsService {
    private final UserRepository userRepository;
    private final TransactionTemplate readOnlyTransactionTemplate;
    private final Scheduler jdbcScheduler;

    @Autowired
    public UserDetailsService(UserRepository userRepository,
                              PlatformTransactionManager transactionManager,
                              @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        Assert.notNull(transactionManager, "The 'transactionManager' argument must not be null.");

        this.userRepository = userRepository;
        this.readOnlyTransactionTemplate = new TransactionTemplate(transactionManager);
        this.readOnlyTransactionTemplate.setReadOnly(true);
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Mono<UserDetails> findByUsername(final String username) {
        return Mono.fromCallable(new Callable<UserDetails>() {
            @Override
            public UserDetails call() throws Exception {
                return readOnlyTransactionTemplate.execute(new TransactionCallback<UserDetails>() {
                    @Override
                    public UserDetails doInTransaction(TransactionStatus transactionStatus) {
                        return userRepository.findByUserName(username);
                    }
                });
            }
        }).subscribeOn(jdbcScheduler);
    }
}
