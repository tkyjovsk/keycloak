package org.keycloak.performance.templates.idm;

import org.keycloak.performance.dataset.attr.AttributeMap;
import org.keycloak.performance.dataset.idm.Group;
import org.keycloak.performance.dataset.idm.Realm;
import org.keycloak.performance.templates.NestedIndexedEntityTemplate;
import org.keycloak.performance.templates.NestedIndexedEntityTemplateWrapperList;
import org.keycloak.performance.templates.attr.StringListAttributeTemplate;
import org.keycloak.representations.idm.GroupRepresentation;

/**
 *
 * @author tkyjovsk
 */
public class GroupTemplate extends NestedIndexedEntityTemplate<Realm, Group, GroupRepresentation> {

    GroupAttributeTemplate attributeTemplate;

    private final int groupsPerRealm;

    public GroupTemplate(RealmTemplate realmTemplate) {
        super(realmTemplate);
        this.attributeTemplate = new GroupAttributeTemplate();
        this.groupsPerRealm = getConfiguration().getInt("groupsPerRealm", 0);
    }

    @Override
    public Group newEntity(Realm parentEntity, int index) {
        return new Group(parentEntity, index, new GroupRepresentation());
    }

    @Override
    public void processMappings(Group group) {
        group.getRepresentation().setAttributes(new AttributeMap(new NestedIndexedEntityTemplateWrapperList<>(group, attributeTemplate)));
    }

    @Override
    public int getEntityCountPerParent() {
        return getGroupsPerRealm();
    }

    public int getGroupsPerRealm() {
        return groupsPerRealm;
    }

    @Override
    public void validateConfiguration() {

        // sizing
        logger().info(String.format("usersPerRealm: %s", groupsPerRealm));
        validateInt().minValue(groupsPerRealm, 0);

        // mappings
        attributeTemplate.validateConfiguration();
    }

    public class GroupAttributeTemplate extends StringListAttributeTemplate<Group> {

        private final int attributesPerGroup;

        public GroupAttributeTemplate() {
            super(GroupTemplate.this);
            this.attributesPerGroup = getConfiguration().getInt("attributesPerGroup", 0);
        }

        @Override
        public int getEntityCountPerParent() {
            return attributesPerGroup;
        }

        @Override
        public void validateConfiguration() {
            logger().info(String.format("attributesPerGroup: %s", attributesPerGroup));
            validateInt().minValue(attributesPerGroup, 0);
        }

    }

}
