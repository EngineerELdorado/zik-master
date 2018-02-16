<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if($_REQUEST):
$id = $_REQUEST['idu'];
$query = $conn->query("select  users.idu,users.username,users.first_name,last_name,users.country,users.city,users.image, notifications.time, notifications.to, notifications.from,notifications.read,notifications.type, notifications.parent, tracks.id, tracks.title from users join relations on users.idu = relations.subscriber
join notifications on relations.subscriber = notifications.from
join tracks on notifications.parent = tracks.id
 where relations.leader = '$id' ORDER BY notifications.time DESC");
$t = $conn->query("select count(notifications.read) as total  from notifications where notifications.read = 0 && notifications.type !=4  ORDER BY notifications.id DESC");
$fl =  $t->fetch_assoc();
$num = $query->num_rows;

   if($num >0) : 
while($row = $query->fetch_assoc()):
$d = array_merge($fl,$row);
if($row['type'] =='2'){
$d['types'] = 'like';
}
elseif($d['type'] =='1'){
$d['types'] = 'comment';
}
$set['activity'][]= $d;
 $set['success'] = 1;
 endwhile;
 else:
 $set['activity'][]= 'No activity';
 $set['success'] = 0;
 endif;
$val = json_encode($set);
echo $val;
endif;
?>  
