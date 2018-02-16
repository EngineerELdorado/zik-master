<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
if(isset($_REQUEST['userid'])) :
  $usid = $_REQUEST['userid'];
  $notificationl = $_REQUEST['notificationl'];
  $notificationcmt = $_REQUEST['notificationcmt'];
  $notificationc = $_REQUEST['notificationc'];
  $notificationf = $_REQUEST['notificationf'];
  $email_comment = $_REQUEST['email_comment'];
  $email_like = $_REQUEST['email_like'];
  $email_new_friend = $_REQUEST['email_new_friend'];

  $query = $conn->query("update users set notificationl = '$notificationl', notificationc  = '$notificationcmt', notificationd = '$notificationc', notificationf = '$notificationf', email_comment = '$email_comment', email_like = '$email_like', email_new_friend = '$email_new_friend' where users.idu = '$usid'");
 if($query) :
    $set['changes'][] = 'changes done';
    $set['success'] =1;
 	else : 
    $set['changes'][] = 'changes fail';
    $set['success'] =0;
endif;
$val = json_encode($set);
echo $val;
endif;
?>