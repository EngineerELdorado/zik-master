<?php 
require_once('../includes/config.php');
	$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
	mysqli_set_charset( $conn, 'utf8');
if($_REQUEST):
	$id = $_REQUEST['idu'];
	$query = $conn->query("select `notifications`.*  from users,notifications where `users`.`idu` = `notifications`.`to` and `users`.`idu`= $id && notifications.from !='$id' ORDER BY notifications.time DESC ");
	$num = $query->num_rows;

    if($num >0) :
	while($res = $query->fetch_assoc()):
		$from = $res['from'];
		$to = $res['to']; 
		$uid = $res['parent']; 
		$querys = $conn->query("select users.username,users.first_name,users.last_name,users.image from users where `users`.`idu`= $from ");

		$t = $conn->query("select count(notifications.read) as count  from notifications where notifications.to = '".$id."' && notifications.read=0  ORDER BY notifications.id DESC");
		$fl =  $t->fetch_assoc();
		
		while($row = $querys->fetch_assoc()):

			$d = array_merge($fl,$row,$res);
				if($d['type'] =='2'){
				 $d['types'] = 'like';
				}
				elseif($d['type'] =='1'){
				 $d['types'] = 'comment';
				}
				elseif($d['type'] =='4'){
				 $d['types'] = 'friend';
				}
			$set['notification'][]= $d;
			$set['success'] = 1;
		endwhile;
	endwhile;
	else:
		 $set['notification'][]= 'No notification';
		 $set['success'] = 0;
		 endif;
	$val = json_encode($set);
	echo $val;
endif;
?>