package london.secondscreen.livehub.service;

import london.secondscreen.livehub.models.User;
import london.secondscreen.livehub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class UserService {
    private final UserRepository userRepository;
    private final TransactionTemplate readOnlyTransactionTemplate;
    private final Scheduler jdbcScheduler;

    @Autowired
    public UserService(UserRepository userRepository, PlatformTransactionManager transactionManager, @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        Assert.notNull(transactionManager, "The 'transactionManager' argument must not be null.");

        this.userRepository = userRepository;
        this.readOnlyTransactionTemplate = new TransactionTemplate(transactionManager);
        this.readOnlyTransactionTemplate.setReadOnly(true);
        this.jdbcScheduler = jdbcScheduler;
    }

    public Mono<User> findOne(final long userId) {
        return Mono.fromCallable(new Callable<User>() {
            @Override
            public User call() throws Exception {
                return readOnlyTransactionTemplate.execute(new TransactionCallback<User>() {
                    @Override
                    public User doInTransaction(TransactionStatus transactionStatus) {
                        return userRepository.findById(userId).orElse(null);
                    }
                });
            }
        }).subscribeOn(jdbcScheduler);
    }

    public User findSyncOne(final long userId) {
        return readOnlyTransactionTemplate.execute(new TransactionCallback<User>() {
            @Override
            public User doInTransaction(TransactionStatus transactionStatus) {
                return userRepository.findById(userId).orElse(null);
            }
        });
    }
}
