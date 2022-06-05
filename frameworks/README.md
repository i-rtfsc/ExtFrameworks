# 在对应target里的device.mk
# 如 build/target/product/sdk_phone_x86_64.mk
$(call inherit-product-if-exists, jos/frameworks/frameworks_ext.mk)

# android 12之后 SKIP_BOOT_JARS_CHECK = true 不生效
# 在文件的最后 build/soong/scripts/check_boot_jars/package_allowed_list.txt
# 加如下：
###################################################
# Packages in the journeyOS namespace across all bootclasspath jars.
system\.ext.*
system\.ext\..*
journeyOS\.os.*
journeyOS\.os\..*
com\.journeyOS.*
com\.journeyOS\..*