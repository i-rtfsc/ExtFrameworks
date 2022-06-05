# build jos-framework.jar
# build jos-services.jar
PRODUCT_PACKAGES += \
    jos-framework \
    jos-services

PRODUCT_BOOT_JARS += jos-framework

#SKIP_BOOT_JARS_CHECK := true
# android 12
# build/soong/scripts/check_boot_jars/package_allowed_list.txt

PRODUCT_SYSTEM_SERVER_JARS += jos-services

# /system_ext/ect/permissions/jos-feature.xml
PRODUCT_PACKAGES += \
    jos-feature.xml