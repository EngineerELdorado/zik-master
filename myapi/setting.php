<?php 
include_once 'db.php';
class setting extends db
{
  
  function __construct()
  {
      parent::__construct();

  }

  function general($fname,$lname,$email,$country,$city,$website,$profile,$chatstatus,$description)
  {
          
  } 
}
$obj = new setting();
if($_GET['general']=='general') :
$obj->gerenal($fn,$ln,$em,$cnty,$cty,$wb,$pro,$cht,$descr);
endif;
?>