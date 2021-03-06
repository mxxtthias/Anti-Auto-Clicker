package de.luzifer.core.checks;

import de.luzifer.core.api.check.Check;
import de.luzifer.core.api.enums.ViolationType;
import de.luzifer.core.api.player.User;
import de.luzifer.core.utils.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DoubleClickCheck extends Check {

    public static final HashMap<User, List<Long>> latestClicks = new HashMap<>();

    @Override
    public void execute(User user) {

        if(user.getClicks() >= Variables.DCcheckAtClicks) {
            if(!latestClicks.containsKey(user)) {
                latestClicks.put(user, new ArrayList<>());
            }

            List<Long> list = latestClicks.get(user);

            List<Long> everyFirst = new ArrayList<>();
            List<Long> everySecond = new ArrayList<>();

            for(int i = 0; i < list.size(); i+=2) {
                Long first = list.get(i);
                Long second;
                if(list.size() > i+1) {
                    second = list.get(i+1);
                } else {
                    second = 0L;
                }
                everyFirst.add(first);
                everySecond.add(second);
            }

            if(everyFirst.size() > 2 && everySecond.size() > 2) {
                long first = everyFirst.get(0);
                long second = everySecond.get(0);
                boolean same = true;
                if(second-first == 0 || second-first == 1) {
                    for(int i = 1; i != everySecond.size(); i++) {
                        if(everySecond.get(i)-everyFirst.get(i) != 0 && everySecond.get(i)-everyFirst.get(i) != 1) {
                            same = false;
                        }
                    }
                } else {
                    if(everyFirst.get(1)-second == 0 || everyFirst.get(1)-second == 1) {
                        for(int i = 1; i != everyFirst.size(); i++) {
                            if(i+1 < everyFirst.size()) {
                                if(everyFirst.get(i+1)-everySecond.get(i) != 0 && everyFirst.get(i+1)-everySecond.get(i) != 1) {
                                    same = false;
                                }
                            }
                        }
                    } else {
                        same = false;
                    }
                }
                if(same) {
                    user.addViolation(ViolationType.NORMAL);
                    if(!(Variables.sanctionateAtViolations > 0)) {
                        user.sanction(false, User.CheckType.DOUBLE_CLICK);
                    }
                }
            }

            list.removeAll(everyFirst);
            list.removeAll(everySecond);

            latestClicks.put(user, list);
        }

    }
}
