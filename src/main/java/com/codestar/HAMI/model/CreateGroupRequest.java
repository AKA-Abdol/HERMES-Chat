package com.codestar.HAMI.model;

import com.codestar.HAMI.entity.File;
import com.codestar.HAMI.entity.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Setter
@Getter
public class CreateGroupRequest {
    private String name;

    private File photo;

    private ArrayList<Long> profileIds;

}
