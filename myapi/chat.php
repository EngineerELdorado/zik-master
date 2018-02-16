<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if($_REQUEST):
$from = $_REQUEST['from'];
$to = $_REQUEST['to'];
$t = $_REQUEST['from'];
$f = $_REQUEST['to'];
$query = $conn->query("Select distinct chat.message,`chat`.* from chat where chat.from ='$from' && chat.to ='$to' || chat.to ='$t' && chat.from='$f'");
$t = $conn->query("select count(chat.read) as count  from chat where chat.to = '$to' && chat.read = 0  ORDER BY chat.id DESC");
$fl =  $t->fetch_assoc();
if(mysqli_num_rows($query) > 0){
  while($row = $query->fetch_assoc()):
$d = array_merge($fl,$row);
           $set['chat'][] = array('count'=>$d['count'],'message'=>html_entity_decode($d['message']),'id'=>$d['id'],'from'=>$d['from'],'to'=>$d['to'],'read'=>$d['read'],'time'=>$d['time']);
          $set['success'] = 1;
 endwhile;
}
else{
 $set['chat'][]='Chat is empty';
 $set['success'] = 0;
}
endif;
$val = json_encode($set);
echo $val;
?>