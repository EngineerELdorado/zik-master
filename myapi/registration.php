<?php 
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
 
$path='../uploads/avatars/';
$imgt='default.png';
if(isset($_REQUEST['username'])) :
  $username = $_REQUEST['username'];
  $email = $_REQUEST['email'];
  $q = mysqli_query($db,"select * from users where username='$username' || email = '$email'");
  if(mysqli_num_rows($q) > 0) : 
             $res['registration'] = 'username or email already existed';
             $res['success'] =0;

  else :
$pass = md5($_REQUEST['pass']);
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
  $qry =  mysqli_query($db,"insert into users(username,password,email,date,image,cover,notificationl,notificationc,notificationd,notificationf) value('$username','$pass','$email',CURDATE(),'$imgt','default.png','1','1','1','1')");
  if($qry) : 
   $to = $email;
   $subject = 'New Registeration';
   $message .= 'Hello  '.$username."<br>";
   $message .= 'Welcome on <a href="http://singering.com//welcome">Singering</a>';
   $header = "From: no-reply@singering.com". "\r\n";
   $header .= "MIME-Version: 1.0". "\r\n";
   $header .= "Content-type:text/html;charset=UTF-8" . "\r\n";
   mail($to, $subject, $message, $header);
   
  $res['registration'] = 'Signup done';
  $res['success'] =1;
  else : 
  $res['registration'] = 'Signup fail';
  $res['success'] =0;
 endif;
  endif;
echo json_encode($res);
endif;
?>