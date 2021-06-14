package com.avast.server.instaprofiles;

import com.avast.server.instaprofiles.service.SynchronizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ExitCodeExceptionMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class InstaProfilesSyncApplication implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InstaProfilesSyncApplication.class);
    final SynchronizationService synchronizationService;

    public InstaProfilesSyncApplication(SynchronizationService synchronizationService) {
        this.synchronizationService = synchronizationService;
    }

    public static void main(String[] args) {
        SpringApplication.run(InstaProfilesSyncApplication.class, args);
    }

    @Bean
    ExitCodeExceptionMapper exitCodeMapper() {
        return exception -> {
            return 1;
        };
    }

    @Override
    public void run(ApplicationArguments args) {
        if (args.containsOption("help")) {
            System.out.println("Command line launch options:");
            System.out.println("--list=<path/profiles.yaml>           stores current TC insta profiles into configuration file");
            System.out.println("--dry-run                             don't apply any changes into TC, just show changes");
            System.out.println("--profilesFile=<path/profiles.yaml>   don't load profiles.yaml from GIT, but from the local file system instead - used for devel and debug purposes");
            System.out.println("--accountsFile=<path/accounts.yaml>   path to accounts file");
            System.out.println("--accountsPk=<path/publicKey>         path to public key PEM");
            return;
        }

        if (args.containsOption("list")) {
            synchronizationService.createInitList(args.getOptionValues("list").get(0));
        } else {
            if (args.containsOption("accountsFile")) {
                final Path accountsPath = Paths.get(args.getOptionValues("accountsFile").get(0));
                final Path pkPath = Paths.get(args.getOptionValues("accountsPk").get(0));

                synchronizationService.updateAccounts(accountsPath, pkPath).block();
            } else {
                final boolean isDryRun = args.containsOption("dry-run") || args.containsOption("dryRun");
                final String profilesFile = args.containsOption("profilesFile") ? args.getOptionValues("profilesFile").get(0) : null;
                synchronizationService.doSynchronize(isDryRun, profilesFile).
                        doOnNext(result -> logger.info("Synchronization result:\n{}", result.toYamlString(isDryRun))).
                        subscribeOn(Schedulers.boundedElastic()).
                        block();
            }
        }
    }

}

