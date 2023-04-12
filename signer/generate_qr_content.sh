SIGNATURE_CHECKSUM=$(/Users/Miftah\ Amirul\ Amin/AppData/Local/Android/Sdk/build-tools/33.0.2/apksigner.bat verify -print-certs ..app/release/app-release.apk | perl -nle "print $& if m{(?<=SHA-256 digest:) .*}"| xxd -r -p | openssl base64 | tr -d '=' | tr -- '+/=' '-_')
URL_APK="https://www.googleapis.com/drive/v3/files/1H_bbobhY_GrxdkwszDnIOeK34M3f2MS-?alt=media&key=AIzaSyAA9ERw-9LZVEohRYtCWka_TQc6oXmvcVU&supportsAllDrives=True"

JSON_PAYLOAD='{"android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME":"com.example.vinstallment/com.example.vinstallment.Receiver.DeviceAdminReceiver",
"android.app.extra.PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM":"%s",
"android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION":"%s"}'

printf "$JSON_PAYLOAD" "$SIGNATURE_CHECKSUM" "$URL_APK"
#
#JSON_FMT='{"bucketname":"%s","objectname":"%s","targetlocation":"%s"}\n'
#printf "$JSON_FMT" "$BUCKET_NAME" "$OBJECT_NAME" "$TARGET_LOCATION"
#keytool -list -printcert -jarfile ../app/release/app-release.apk | perl -nle "print $& if m{(?<=SHA256:) .*}" | xxd -r -p | openssl base64 | tr -- '+' '-_'keytool -list -printcert -jarfile ../app/release/app-release.apk | perl -nle "print $& if m{(?<=SHA256:) .*}" | xxd -r -p | openssl base64 | tr -- '+' '-_'