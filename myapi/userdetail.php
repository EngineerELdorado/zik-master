<?php 
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $db, 'utf8');
if(isset($_REQUEST['login']) && $_REQUEST['login']=='true' && $_REQUEST['uid']!='') :
	$id = $_REQUEST['uid'];
$query = $db->query("SELECT * FROM users where idu = '$id'");

$num = $query->num_rows;
if($num > 0) :
     while($row = $query->fetch_object()) :
     $set['usersd'][]=$row;
     $set['success']=1;
     endwhile;
	else :
        $set['usersd'][]='Users No Found';
        $set['success']=0;
		endif;
$val = json_encode($set);
echo $val;
endif;
?>