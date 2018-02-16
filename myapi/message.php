<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['from'])){
$from = $_REQUEST['from'];
$to = $_REQUEST['to'];
$message = $_REQUEST['message'];
$query = $conn->query("insert into chat (chat.from,chat.to,message)VALUES('$from','$to','$message')");
}
 if($query){
          $set['success'] = 1;
}
else{
 $set['message'][]='Message Not Sent';
 $set['success']= 0;
}
$val = json_encode($set);
echo $val;
?>