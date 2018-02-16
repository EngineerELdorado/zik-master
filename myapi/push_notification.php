<?php
function fcm($token,$message)
		{		
		// API access key from Google API's Console
		$server_key = 'AAAAk40bNiE:APA91bGOvWosLbIXNH2MkEbDhYgZKhBZHFDgzywNY2xhcNyumTm9VRUGE5BUslru5OgPHE4ZBz1x0ye1I1VJmqv4uIcjijZrLZHN67vbdXDriEMGl2sRIcj369tItnWvnjScDQGKU8Oy';
		//define( 'API_ACCESS_KEY', 'AIzaSyDGOyKUHTNP2xXZMKMnzccfiH0yRjNoGu8' );
		 
		
		      $fields = array (
                "registration_ids" =>$token,
                 'notification' =>$message,
                 "sound" => "default"
                 );
		$headers = array
		(
			'Authorization: key=' . $server_key,
			'Content-Type: application/json'
		);
		 
		$ch = curl_init();
		curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
		curl_setopt( $ch,CURLOPT_POST, true );
		curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
		curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
		curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
		curl_setopt($ch, CURLOPT_POSTFIELDS,json_encode($fields));
		$result = curl_exec($ch );
		curl_close( $ch );
		echo $result;
		}
		
		

require_once('../includes/config.php');
	$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
	mysqli_set_charset( $conn, 'utf8');

	$id = $_REQUEST['idu'];
	$query = $conn->query("select `notifications`.*  from users,notifications where `users`.`idu` = `notifications`.`to` and  `notifications`.`read` = '0' ORDER BY notifications.time DESC ");
	$num = $query->num_rows;
	  
      while($row = $query->fetch_array())
	  {
		 if($row['type'] == '1'){
			$message = ' commented on track';
		 }
		 elseif($row['type'] == '2'){
			$message = ' liked your track';
		 }
		 elseif($row['type'] == '4'){
			$message = ' added you as a friend';
		 }
		  $pending[] = $row['to'];
		  
	  }	 
	   
	   foreach($pending as $to)
	   {
		  $query = $conn->query("select * from users where idu = '$to'");
          while($token = $query->fetch_assoc())
		  {
			$name = $token['username'];
			 $tokens[] = $token['device_token']; 
		  }	
	   }
       	$data = array('body'=>$message);
	    fcm($tokens,$data);   
	
?>