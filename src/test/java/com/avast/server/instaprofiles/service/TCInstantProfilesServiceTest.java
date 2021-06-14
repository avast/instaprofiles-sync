package com.avast.server.instaprofiles.service;

import com.avast.server.instaprofiles.model.SynchronizeResult;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Vitasek L.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
@Ignore
public class TCInstantProfilesServiceTest {

    @Autowired
    SynchronizationService service;

    @Test
    public void doSync() {
        final SynchronizeResult block = service.doSynchronize(false, null).block();
        assert block != null;
        System.out.println("createdProfiles = " + block.toYamlString(false));
    }
}