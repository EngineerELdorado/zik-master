<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['idu'])):
$id = $_REQUEST['idu'];
$query = $conn->query("select users.idu,users.username,users.first_name,last_name,users.country,users.city,users.image,users.logout_time,users.online from relations join users on users.idu = relations.leader   where relations.subscriber = '$id' && users.logout_time = '0' &&  users.offline = '0' ORDER BY users.online DESC");
	$num = $query->num_rows;
	if($num >0) :
	while($row = $query->fetch_assoc()) :
	$inf['online'][] = $row;
	$inf['success'] =1;
    endwhile;
    else:
	$inf['online'][] = 'Nobody Online';
	$inf['success'] =0;
	endif;
	$val = json_encode($inf);
	echo $val;
	endif;
?>