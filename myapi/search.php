<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
$search = htmlspecialchars($_REQUEST['search']);
$query = $conn->query("Select idu,username,first_name,last_name,country,city,image from users where first_name like '%$search%' ||  last_name like '%$search%' || username like '%$search%'");
$num = $query->num_rows;
if($num > 0) :
  while($row = $query->fetch_object()):
           $set['search'][] = $row;
          $set['success'] = 1;
 endwhile;
else :
          $set['search'][] = "No Found";
          $set['success'] = 0;
endif;
$val = json_encode($set);
echo $val;
?>