<?php 
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
function FCM($registratoin_ids,$data)
{
define("GOOGLE_API_KEY","AAAAk40bNiE:APA91bGOvWosLbIXNH2MkEbDhYgZKhBZHFDgzywNY2xhcNyumTm9VRUGE5BUslru5OgPHE4ZBz1x0ye1I1VJmqv4uIcjijZrLZHN67vbdXDriEMGl2sRIcj369tItnWvnjScDQGKU8Oy");	
			$url = "https://fcm.googleapis.com/fcm/send";
			
			$fields = array (
                "registration_ids" => $registratoin_ids,
                'notification' =>$data
            );
			
			$headers = array(
                "Authorization: key=".GOOGLE_API_KEY,
                "Content-Type: application/json"
            );
			$ch = curl_init();
            curl_setopt($ch, CURLOPT_URL, $url);
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
            curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
            curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($fields));
            $result_fcm=curl_exec($ch);
			print_r($result_fcm);
			//if($result_gcm===FALSE) {
              //  die("Curl failed: ".curl_error($ch));
            //}
            curl_close($ch);		
}

$t[]='eVU_N4pxduU:APA91bH9wRnn8cSY679plkg29ckdq2Jf7ktDBPJw7dWe5FtTcfMquthUAd8bWt4yS0BDuJNS_fI8DfQqLpf4ynUVARo5Qt0cISqf0_sMV0VgD9qXmb0Y5Zs1gyvQarKe6K0JkQVZYRIu';
	      $m = array('body'=>'like track');
FCM($t,$m);

?>