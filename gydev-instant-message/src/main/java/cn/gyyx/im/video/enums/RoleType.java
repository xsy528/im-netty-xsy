package cn.gyyx.im.video.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@AllArgsConstructor
public enum RoleType {
    NormalUser("user"),
    CustomUser("custom"),
    Administrator("administrator"),
    ;

    private String role;

    public static RoleType search(String roleType) {
        Optional<RoleType> findFirst = Arrays.stream(RoleType.values())
                .filter(p -> roleType.equalsIgnoreCase(p.getRole()))
                .findFirst();
        return findFirst.orElse(null);
    }
}
