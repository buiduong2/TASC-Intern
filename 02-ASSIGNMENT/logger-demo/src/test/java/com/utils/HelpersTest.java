package com.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

public class HelpersTest {

    @Test
    public void testTrunkIndexes() {

        List<List<Long>> trunk = Helpers.trunkIndexes(10, 3);

        assertThat(trunk).isNotEmpty()

                .allMatch(indexses -> indexses.size() == 2);
        assertThat(trunk.get(0).get(0)).isEqualTo(0);

        long last = trunk.get(0).get(1);
        System.out.println(trunk);
        for (int i = 1; i < trunk.size(); i++) {
            List<Long> indexses = trunk.get(i);
            assertThat(last + 1).isEqualTo(indexses.get(0));
            last = indexses.get(1);
        }

    }
}
