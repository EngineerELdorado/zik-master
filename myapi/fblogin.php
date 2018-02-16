<?php 
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
$path='../uploads/avatars/';
$imgt='default.png';
if(isset($_REQUEST['email'])) :
       $username= $_REQUEST['firstname'];
	$firstname= $_REQUEST['firstname'];
	$lastname= $_REQUEST['lastname'];
	$email = $_REQUEST['email'];
	$pass = md5($_REQUEST['firstname'].$_REQUEST['lastname']);
	$q = mysqli_query($db,"select * from users where email = '$email'");  
  if(mysqli_num_rows($q) > 0) :  
     while($row = mysqli_fetch_assoc($q)) :
	  $res['registration'][] = $row;
	  $res['success'] =1;
	  endwhile;
	else:
	if($_REQUEST['user_image']!='') :
	$base=$_REQUEST['user_image'];
	$binary=base64_decode($base);
	header('Content-Type: bitmap; charset=utf-8');
	$time=time();
	$imgt=$time.'.jpg';
	$file = fopen($path.$imgt, 'wb');
	$img=fwrite($file,$binary);
	fclose($file);
	endif;
	  $qry =  mysqli_query($db,"insert into users(username,password,email,first_name,last_name,date,image,cover,notificationl,notificationc,notificationd,notificationf,email_comment,email_like,email_new_friend) value('$firstname','$pass','$email','$firstname','$lastname','CURDATE()','$imgt','default.png','1','1','1','1','1','1','1')");
	 
	  $last_id = $db->insert_id;
	  $result = mysqli_query($db,"select * from users where idu = '$last_id'"); 
	   while($rows = mysqli_fetch_assoc($result)) :
	  $res['registration'][] = $rows;
	  $res['success'] =1;
	  endwhile;
	  endif;
	  echo json_encode($res);
endif;
?>