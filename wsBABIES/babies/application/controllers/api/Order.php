<?php

defined('BASEPATH') OR exit('No direct script access allowed');

// This can be removed if you use __autoload() in config.php OR use Modular Extensions
require APPPATH . '/libraries/REST_Controller.php';

/**
 * This is an example of a few basic event interaction methods you could use
 * all done with a hardcoded array
 *
 * @package         CodeIgniter
 * @subpackage      Rest Server
 * @category        Controller
 * @author          Phil Sturgeon, Chris Kacerguis
 * @license         MIT
 * @link            https://github.com/chriskacerguis/codeigniter-restserver
 */
class Order extends REST_Controller {

    function __construct()
    {
        // Construct the parent class
        parent::__construct();

        // Configure limits on our controller methods
        // Ensure you have created the 'limits' table and enabled 'limits' within application/config/rest.php
        $this->methods['event_post']['limit'] = 500000000; // 500 requests per hour per event/key
        // $this->methods['event_delete']['limit'] = 50; // 50 requests per hour per event/key
        $this->methods['event_get']['limit'] = 500000000; // 500 requests per hour per event/key

        header("Access-Control-Allow-Origin: *");
        header("Access-Control-Allow-Methods: GET, POST");
        header("Access-Control-Allow-Headers: Origin, Content-Type, Accept, Authorization");
    }

	function ellipsis($string) {
        $cut = 30;
        $out = strlen($string) > $cut ? substr($string,0,$cut)."..." : $string;
        return $out;
    }
	
    function clean($string) {
        return preg_replace("/[^[:alnum:][:space:]]/u", '', $string); // Replaces multiple hyphens with single one.
    }

    function error($string) {
        return str_replace( array("\t", "\n", "\r") , "", $string);
    }

    public function send_gcm($registrationId,$message,$title,$fragment,$nomor,$nama)
    {
        $this->load->library('gcm');

        $this->gcm->setMessage($message);
        $this->gcm->setTitle($title);
        $this->gcm->setFragment($fragment);
        $this->gcm->setNomor($nomor);
        $this->gcm->setNama($nama);

        $this->gcm->addRecepient($registrationId);

        $this->gcm->setData(array(
            'some_key' => 'some_val'
        ));

        $this->gcm->setTtl(500);
        $this->gcm->setTtl(false);

        $this->gcm->setGroup('Test');
        $this->gcm->setGroup(false);

        $this->gcm->send();

       if ($this->gcm->send())
           echo 'Success for all messages';
       else
           echo 'Some messages have errors';

       print_r($this->gcm->status);
       print_r($this->gcm->messagesStatuses);

        die(' Worked.');
    }

	public function send_gcm_group($registrationId,$message,$title,$fragment,$nomor,$nama)
    {
        $this->load->library('gcm');

        $this->gcm->setMessage($message);
        $this->gcm->setTitle($title);
        $this->gcm->setFragment($fragment);
        $this->gcm->setNomor($nomor);
        $this->gcm->setNama($nama);

        foreach ($registrationId as $regisID) {
            $this->gcm->addRecepient($regisID);
        }

        $this->gcm->setTtl(500);
        $this->gcm->setTtl(false);

        $this->gcm->setGroup('Test');
        $this->gcm->setGroup(false);

        $this->gcm->send();
    }

    function test_get()
    {
        
        $result = "a";
        
        $data['data'] = array();

        $new_message ="lalalalala";
        
        // START SEND NOTIFICATION
        $vcGCMId = 'ekSgHWHC8WY:APA91bGoAT0AY_3xXxWStnmNx9ETlNftaQFrKPdwYKP2yhxvd9mMKbRJL6ORaplS3MbPdkj36N_SGR-9hgzNcDED1yaSxQYBj2oCY2ZSN3wduMZvxXgSgUIxqYMuOBtAfLYPk5g96zGd';

        $this->send_gcm($vcGCMId, $this->ellipsis($new_message),'New Message(s) From ','PrivateMessage','0','0');
        

        
        $this->response($vcGCMId);
    }

    function notifyNewPraorder($kode)
    {
        $regisID = array();
        $query = "SELECT 
                    #nomor,
                    #nama,
                    #kode,
                    #role_android,
                    gcm_id 
                    FROM mhadmin 
                    WHERE admin = 1 
                    AND gcm_id IS NOT NULL
                    AND status_aktif = 1";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                $vcGCMId = $r['gcm_id'];
                if( $vcGCMId != "null" ){      
                    array_push($regisID, $vcGCMId);       
                }

            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        $this->send_gcm_group($regisID, $this->ellipsis("Kode : ".$kode),'New Pra Order','1','','');

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        } 
    }

    function notifyNewOrderjual($kode)
    {
        $regisID = array();
        $query = "SELECT 
                    #nomor,
                    #nama,
                    #kode,
                    #role_android,
                    gcm_id 
                    FROM mhadmin 
                    WHERE admin = 1 
                    AND gcm_id IS NOT NULL
                    AND status_aktif = 1";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                $vcGCMId = $r['gcm_id'];
                if( $vcGCMId != "null" ){      
                    array_push($regisID, $vcGCMId);       
                }

            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        $this->send_gcm_group($regisID, $this->ellipsis("Kode : ".$kode),'New Order Jual','2','','');

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function testNotif_get()
    {
        echo "string";
        $this -> notifyNewPraorder("poj lala");
        //$this -> notifyNewOrderjual("oj baba");
    }

 //    //added by Tonny
 //    //untuk mendapatkan nomor salesorder header yg sekarang
 //    function getNomorCountHeader_post()
 //    {
 //        $data['data'] = array();
 //        $value = file_get_contents('php://input');
 //        $jsonObject = (json_decode($value , true));
 //        $kodecount = (isset($jsonObject["kodecount"]) ? $this->clean($jsonObject["kodecount"])     : "");
 //        $query = "SELECT akhir counter FROM tcount WHERE kode = '$kodecount'";
 //        $result = $this->db->query($query);
 //        if($result && $result->num_rows() > 0){
 //            foreach ($result->result_array() as $r){
 //                array_push($data['data'], array(
 //                                                'counter'		=> $r['counter']
 //                                                )
 //                );
 //            }
 //        }else{
 //            array_push($data['data'], array( 'query' => $this->error($query) ));
 //        }
 //    }

	// // --- POST insert new order jual --- //
	// //added by Tonny
	// function insertNewOrderJual_post()
	// {
 //        $data['data'] = array();

 //        $value = file_get_contents('php://input');
	// 	$jsonObject = (json_decode($value , true));

 //        ////////////////////////////////////////////////////////////////////////////////HEADER/////////////////////////////////////////////////////////////////////////////
 //        $nomorcustomer = (isset($jsonObject["nomorcustomer"]) ? $this->clean($jsonObject["nomorcustomer"])     : "");
 //        $kodecustomer = (isset($jsonObject["kodecustomer"]) ? $this->clean($jsonObject["kodecustomer"])     : "");
 //        $nomorbroker = (isset($jsonObject["nomorbroker"]) ? $this->clean($jsonObject["nomorbroker"])     : "");
 //        $kodebroker = (isset($jsonObject["kodebroker"]) ? $this->clean($jsonObject["kodebroker"])     : "");
 //        $nomorsales  = (isset($jsonObject["nomorsales"]) ? $this->clean($jsonObject["nomorsales"])     : "");
 //        $kodesales = (isset($jsonObject["kodesales"]) ? $this->clean($jsonObject["kodesales"])     : "");
 //        $subtotal = (isset($jsonObject["subtotal"]) ? $this->clean($jsonObject["subtotal"])     : "");
 //        $subtotaljasa = (isset($jsonObject["subtotaljasa"]) ? $this->clean($jsonObject["subtotaljasa"])     : "");
 //        $subtotalbiaya = (isset($jsonObject["subtotalbiaya"]) ? $this->clean($jsonObject["subtotalbiaya"])     : "");
 //        $disc = (isset($jsonObject["disc"]) ? $this->clean($jsonObject["disc"])     : "0");
 //        $discnominal = (isset($jsonObject["discnominal"]) ? $this->clean($jsonObject["discnominal"])     : "0");
 //        $dpp = (isset($jsonObject["dpp"]) ? $this->clean($jsonObject["dpp"])     : "0");
 //        $ppn = (isset($jsonObject["ppn"]) ? $this->clean($jsonObject["ppn"])     : "0");
 //        $ppnnominal = (isset($jsonObject["ppnnominal"]) ? $this->clean($jsonObject["ppnnominal"])     : "");
 //        $total = (isset($jsonObject["total"]) ? $this->clean($jsonObject["total"])     : "");
 //        $totalrp = (isset($jsonObject["totalrp"]) ? $this->clean($jsonObject["totalrp"])     : "");
 //        $pembuat = (isset($jsonObject["pembuat"]) ? $this->clean($jsonObject["pembuat"])     : "");
 //        $nomorcabang = (isset($jsonObject["nomorcabang"]) ? $this->clean($jsonObject["nomorcabang"])     : "");
 //        $cabang = (isset($jsonObject["cabang"]) ? $this->clean($jsonObject["cabang"])     : "");
 //        $valuta = (isset($jsonObject["valuta"]) ? $this->clean($jsonObject["valuta"])     : "");
 //        $kurs = (isset($jsonObject["kurs"]) ? $this->clean($jsonObject["kurs"])     : "");
 //        $jenispenjualan = (isset($jsonObject["jenispenjualan"]) ? $this->clean($jsonObject["jenispenjualan"])     : "");
 //        $isbarangimport = (isset($jsonObject["isbarangimport"]) ? $this->clean($jsonObject["isbarangimport"])     : "");
 //        $isppn = (isset($jsonObject["isppn"]) ? $this->clean($jsonObject["isppn"])     : "");
 //        $proyek = (isset($jsonObject["proyek"]) ? $this->clean($jsonObject["proyek"])     : "");
 //        $user = (isset($jsonObject["user"]) ? $this->clean($jsonObject["user"])     : "");

	// 	$this->db->trans_begin();

 //        $query = "INSERT INTO thorderjual (
 //                    Tanggal,
 //                    NomorCustomer,
 //                    KodeCustomer,
 //                    NomorBroker,
 //                    KodeBroker,
 //                    NomorSales,
 //                    KodeSales,
 //                    SubTotal,
 //                    SubtotalJasa,
 //                    SubtotalBiaya,
 //                    Disc,
 //                    DiscNominal,
 //                    DPP,
 //                    PPN,
 //                    PPNNominal,
 //                    Total,
 //                    TotalRp,
 //                    Pembuat,
 //                    NomorCabang,
 //                    Cabang,
 //                    Booking,
 //                    Valuta,
 //                    Kurs,
 //                    JenisPenjualan,
 //                    Proyek,
 //                    IsBarangImport,
 //                    IsPPN,
 //                    status,
 //                    dibuat_oleh,
 //                    dibuat_pada)
 //                  VALUES (
 //                    NOW(),
 //                    $nomorcustomer,
 //                    '$kodecustomer',
 //                    $nomorbroker,
 //                    '$kodebroker',
 //                    $nomorsales,
 //                    '$kodesales',
 //                    $subtotal,
 //                    $subtotaljasa,
 //                    $subtotalbiaya,
 //                    $disc,
 //                    $discnominal,
 //                    $dpp,
 //                    $ppn,
 //                    $ppnnominal,
 //                    $total,
 //                    $totalrp,
 //                    '$pembuat',
 //                    $nomorcabang,
 //                    '$cabang',
 //                    0,
 //                    '$valuta',
 //                    $kurs,
 //                    '$jenispenjualan',
 //                    $proyek,
 //                    $isbarangimport,
 //                    $isppn,
 //                    1,
 //                    $user,
 //                    NOW())";
 //        $this->db->query($query);

 //        $interval  = $this->db->query("SELECT intnilai FROM whsetting_mobile WHERE intNomor = 1 LIMIT 1")->row()->intnilai;
 //        $rows =  $this->db->insert_id();
 //        array_push($data['data'], array( 'query' => $rows ));
 //        $nomor = $rows['nomor'];

 //        ////////////////////////////////////////////////////////////////////////////////DETAIL/////////////////////////////////////////////////////////////////////////////
 //        $noitem = FALSE;
 //        $dataitemdetail = (isset($jsonObject["dataitemdetail"]) ? $jsonObject["dataitemdetail"]     : "");
 //        $datapekerjaandetail = (isset($jsonObject["datapekerjaandetail"]) ? $jsonObject["datapekerjaandetail"]     : "");

 //        $pieces = explode('|', $dataitemdetail);
 //        if (count($pieces) > 1){
 //            for ($i = 0; $i < count($pieces); $i++) {
 //                if($pieces[$i]!="")
 //                {
 //                    $parts = explode("~", $pieces[$i]);
 //                    //nomorbarang~kodebarang~namabarang~nomorbarangreal~kodebarangreal~namabarangreal~satuan~harga~qty~fee~disc~subtotal~notes
 //                    $nomorheader = $nomor;
 //                    $nomorbarang = $parts[0];
 //                    $kodebarang = $parts[1];
 //                    $nomorbarangreal = $parts[3];
 //                    $kodebarangreal = $parts[4];
 //                    $qty = $parts[8];
 //                    $jumlah = $parts[8];
 //                    $harga = $parts[7];
 //                    $fee = $parts[9];
 //                    $hargamandor = $parts[9];
 //                    $disc1 = $parts[10];
 //                    $disc1nominal = $harga * $disc1 / 100 ;
 //                    $netto = $harga - $disc1nominal + $fee;
 //                    $subtotald = $parts[11];
 //                    $nomorbarangjual = $nomorbarang;
 //                    $kodebarangjual = $kodebarang;
 //                    $keterangandetail = $parts[12];

 //                    //insert barang
 //                    $query = "INSERT INTO tdorderjual (
 //                                NomorHeader,
 //                                NomorBarang,
 //                                KodeBarang,
 //                                Qty,
 //                                Jumlah,
 //                                Harga,
 //                                Fee,
 //                                HargaMandor,
 //                                Disc1,
 //                                Disc1Nominal,
 //                                Disc2,
 //                                Disc2Nominal,
 //                                Netto,
 //                                Subtotal,
 //                                NomorPekerjaan,
 //                                KodePekerjaan,
 //                                NomorBarangJual,
 //                                KodeBarangJual,
 //                                KeteranganDetail,
 //                                dibuat_oleh,
 //                                dibuat_pada)
 //                              VALUES (
 //                                $nomorheader,
 //                                $nomorbarangreal,
 //                                '$kodebarangreal',
 //                                $qty,
 //                                $jumlah,
 //                                $harga,
 //                                $fee,
 //                                $hargamandor,
 //                                $disc1,
 //                                $disc1nominal,
 //                                0,
 //                                0,
 //                                $netto,
 //                                $subtotald,
 //                                0,
 //                                '',
 //                                $nomorbarangjual,
 //                                '$kodebarangjual',
 //                                '$keterangandetail',
 //                                $user,
 //                                NOW())";
 //                    $this->db->query($query);
 //                }
 //            }
 //        }

 //        $pieces = explode('|', $datapekerjaandetail);
 //        if (count($pieces) > 1){
 //            for ($i = 0; $i < count($pieces); $i++) {
 //                if($pieces[$i]!="")
 //                {
 //                    $parts = explode("~", $pieces[$i]);  //nomorbarang~kodebarang~namabarang~satuan~harga~qty~fee~disc~subtotal~notes
 //                    array_push($data['data'], array( 'query' => $parts ));
 //                    $nomorheader = $nomor;
 //                    $nomorpekerjaan = $parts[0];
 //                    $kodepekerjaan = $parts[1];
 //                    $qty = $parts[5];
 //                    $jumlah = $parts[5];
 //                    $harga = $parts[4];
 //                    $fee = 0;
 //                    $hargamandor = 0;
 //                    $disc1 = 0;
 //                    $disc1nominal = 0;
 //                    $subtotald = $parts[8];
 //                    $keterangandetail = $parts[9];
 //                    $netto = $harga - $disc1nominal + $fee;

 //                    //insert pekerjaan
 //                    $query = "INSERT INTO tdorderjual (
 //                                NomorHeader,
 //                                NomorPekerjaan,
 //                                KodePekerjaan,
 //                                Qty,
 //                                Jumlah,
 //                                Harga,
 //                                Fee,
 //                                HargaMandor,
 //                                Disc1,
 //                                Disc1Nominal,
 //                                Disc2,
 //                                Disc2Nominal,
 //                                Netto,
 //                                Subtotal,
 //                                KeteranganDetail,
 //                                Jasa,
 //                                dibuat_oleh,
 //                                dibuat_pada)
 //                              VALUES (
 //                                $nomorheader,
 //                                $nomorpekerjaan,
 //                                '$kodepekerjaan',
 //                                $qty,
 //                                $jumlah,
 //                                $harga,
 //                                $fee,
 //                                $hargamandor,
 //                                $disc1,
 //                                $disc1nominal,
 //                                0,
 //                                0,
 //                                $netto,
 //                                $subtotald,
 //                                '$keterangandetail',
 //                                1,
 //                                $user,
 //                                NOW())";
 //                    $this->db->query($query);
 //                }
 //            }
 //        }

 //        if ($this->db->trans_status() === FALSE)
 //        {
 //            $this->db->trans_rollback();
 //            array_push($data['data'], array( 'query' => $this->error($query) ));
 //        }else{
 //            $this->db->trans_commit();
 //            array_push($data['data'], array( 'success' => 'true' ));
 //        }
 //        if ($data){
 //            // Set the response and exit
 //            $this->response($data['data']); // OK (200) being the HTTP response code
 //        }
 //    }




// vvv ############################ PRAORDER ################################# vvv

    function getPraOrderList_post(){
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "SELECT
                    a.nomor,
                    a.kode,
                    a.tanggal,
                    b.kode as kodeCustomer,
                    b.nama as namaCustomer,
                    a.keterangan,
                    a.status_disetujui
                    from thjualpraorder a
                    join mhrelasi b
                    on a.nomormhcustomer = b.nomor
                    where a.status_aktif = 1
                    order by a.nomor
                    #order by a.status_disetujui desc";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'tanggal'               => $r['tanggal'],
                                                'kodeCustomer'          => $r['kodeCustomer'],
                                                'namaCustomer'          => $r['namaCustomer'],
                                                'keterangan'            => $r['keterangan'],
                                                'status_disetujui'      => $r['status_disetujui']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

     function getPraOrderSummary_post(){
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $nomor = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");


        // nomormhvaluta ga cocok sama table mhvaluta
        // ppn diskon angka 0 semua
        $query = "SELECT
			        a.nomor,
			        c.nama as namaCabang,
			        d.nomor as nomorSales,
			        d.nama as namaSales,
			        #e.kode as namaValuta,
			        f.nomor as nomorJenisHarga,
			        f.nama as namaJenisHarga,
			        a.kode,
			        a.tanggal,
                    b.nomor as nomorCustomer,
			        b.kode as kodeCustomer,
			        b.nama as namaCustomer,
			        a.ppn_prosentase as ppnPersen,
			        a.ppn_nominal as ppnNom,
			        a.diskon_prosentase as diskonPersen,
			        a.diskon_nominal as diskonNom,
			        a.kurs,
			        a.keterangan,
			        a.status_disetujui,
					g.nama as disetujui_oleh,
					a.disetujui_pada			   
			        from thjualpraorder a
			        join mhrelasi b on a.nomormhcustomer = b.nomor
			        join mhcabang c on a.nomormhcabang = c.nomor
			        join mhsales d on a.nomormhsales = d.nomor
			        #join mhvaluta e on a.nomormhvaluta = e.nomor
			        join mhjenisharga f on a.nomormhjenisharga = f.nomor
			        left join mhadmin g on a.disetujui_oleh = g.nomor
			        where a.status_aktif = 1
			        AND a.nomor = $nomor";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'namaCabang'            => $r['namaCabang'],
                                                'nomorSales'            => $r['nomorSales'],
                                                'namaSales'             => $r['namaSales'],
                                                //'namaValuta'             => $r['namaValuta'],
                                                'nomorJenisHarga'        => $r['nomorJenisHarga'],
                                                'namaJenisHarga'        => $r['namaJenisHarga'],
                                                'kode'                  => $r['kode'],
                                                'tanggal'               => $r['tanggal'],

                                                'nomorCustomer'          => $r['nomorCustomer'],
                                                'kodeCustomer'          => $r['kodeCustomer'],
                                                'namaCustomer'          => $r['namaCustomer'],

                                                'ppnPersen'          	=> $r['ppnPersen'],
                                                'ppnNom'         		=> $r['ppnNom'],
                                                'diskonPersen'          => $r['diskonPersen'],
                                                'diskonNom'         	=> $r['diskonNom'],
                                                'kurs'    		     	=> $r['kurs'],

                                                'keterangan'            => $r['keterangan'],
                                                'status_disetujui'      => $r['status_disetujui'],

                                                'disetujui_oleh'        => $r['disetujui_oleh'],
                                                'disetujui_pada'      	=> $r['disetujui_pada']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function getPraOrderItemList_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $nomor = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");  //input merupakan nomor thorderjual

        //show tdpora order item whre nomor header input user

        $query = "SELECT
					a.nomor,
					b.kode as kodeBarang,
					b.nomor as nomorBarang,
					b.nama as namaBarang,
					c.nomor as nomorSatuan,
					c.nama as satuan,
					a.jumlah
					from tdjualpraorder a
					join mhbarang b on a.nomormhbarang = b.nomor
					join mhsatuan c on a.nomormhsatuan = c.nomor
					where a.status_aktif = 1
					AND a.nomorthjualpraorder = $nomor";

        $result = $this->db->query($query);
        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                                                'nomor'			=> $r['nomor'],
                                                'kodeBarang'	=> $r['kodeBarang'],
                                                'nomorBarang'	=> $r['nomorBarang'],
                                                'namaBarang'	=> $r['namaBarang'],
                                                'nomorSatuan'   => $r['nomorSatuan'],
                                                'satuan'    	=> $r['satuan'],
                                                'jumlah'     	=> $r['jumlah']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    function createNewPraorderKode($namaCabang)
    {
        $newKode = "P-OJ";
        $tgl = substr(date("Ym"),2);
        $query = "SELECT a.kode,a.tanggal
                    from thjualpraorder a
                    #where a.status_aktif = 1
                    order by nomor DESC 
                    limit 2";

        $tempKode['data'] = array();
        $result = $this->db->query($query);
        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($tempKode['data'], array(
                                                'kode'             => $r['kode'],
                                                'tanggal'          => $r['tanggal']                                           
                                                )
                );
            }
        }else{
            array_push($tempKode['data'], array( 'query' => $this->error($query) ));
        }

        $tempDate1 = (int)substr(str_replace("-","", $tempKode['data'][0][tanggal]),0,6);  //echo $tempDate1."";
        $tempDate2 = (int)substr(str_replace("-","", $tempKode['data'][1][tanggal]),0,6);  //echo $tempDate2."";

        // ### pengecekan reset counter untuk ganti bulan
        if($tempDate1 > $tempDate2)
        {
            $strKode = 0; // reset
        }
        else
        {
            $strKode = substr($tempKode['data'][0][kode], -4); // ambil 4 dari belakang
        }

        $counter = (int)$strKode+1; // increment
        //kembalikan ke format angka semula
        $strCounter = (string)$counter;
        if(strlen($strCounter) == 1)
        {
            $strCounter = "0000".$strCounter;
        }
        else if(strlen($strCounter) == 2)
        {
            $strCounter = "000".$strCounter;
        }
        else if(strlen($strCounter) == 3)
        {
            $strCounter = "00".$strCounter;
        }
        //gabungin semua
        $newKode = $newKode."/".$namaCabang."/".$tgl."/".$strCounter;

        return $newKode;
    }


    // ### all insert dari header sampai ke item
    function insertPraorder_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### parameter dari andorid untuk header
        $nomorCabang = (isset($jsonObject["nomorCabang"]) ? $this->clean($jsonObject["nomorCabang"])     : ""); 
        $namaCabang = (isset($jsonObject["namaCabang"]) ? $this->clean($jsonObject["namaCabang"])     : ""); 
        $nomorCustomer = (isset($jsonObject["nomorCustomer"]) ? $this->clean($jsonObject["nomorCustomer"])     : "");
        $nomorSales = (isset($jsonObject["nomorSales"]) ? $this->clean($jsonObject["nomorSales"])     : "");
        $nomorJenisHarga = (isset($jsonObject["nomorJenisHarga"]) ? $this->clean($jsonObject["nomorJenisHarga"])     : "");
        $tanggal =  (isset($jsonObject["tanggal"]) ? $this->clean($jsonObject["tanggal"])     : "");
        $keterangan = (isset($jsonObject["keterangan"]) ? $this->clean($jsonObject["keterangan"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");



// ### create new kode untuk header
        $newKode = $this -> createNewPraorderKode($namaCabang);

// ###

// ### query insert untuk header
        $this->db->trans_begin();
        $query = "INSERT INTO thjualpraorder(
                    nomormhcabang,
                    nomormhcustomer,
                    nomormhsales,
                    nomormhjenisharga,
                    kode,
                    tanggal,
                    kurs,
                    keterangan,
                    status_aktif,
                    dibuat_oleh,
                    dibuat_pada,
                    status_disetujui,
                    disetujui_oleh
                    )
                    VALUES (
                    $nomorCabang,
                    $nomorCustomer,
                    $nomorSales,
                    $nomorJenisHarga,
                    '$newKode',
                    '$tanggal',
                    1,
                    '$keterangan',
                    1,
                    $nomorAdmin,
                    '$todayDateTime',
                    0,
                    0
                    )";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            //array_push($data['data'], array( 'success' => 'true' ));


            $query = " SELECT nomor AS nomorHeader FROM thjualpraorder 
            WHERE status_aktif = 1
            AND kode = '$newKode' ";

            $tempNomorHeader;

            $result = $this->db->query($query);
            if( $result && $result->num_rows() > 0){
                foreach ($result->result_array() as $r){
                    $tempNomorHeader = $r['nomorHeader'];
                }
            }else{
                array_push($data['data'], array( 'query' => $this->error($query) ));
            }

            if($tempNomorHeader > 0)
            {
                // ### berarti header sudah masuk database
                // ### do insert item detail

                // ### get data item dari adroid
                $noitem = FALSE;
                $dataitemdetail = (isset($jsonObject["dataitemdetail"]) ? $jsonObject["dataitemdetail"]     : "");

                // ### bongkar
                $pieces = explode('|', $dataitemdetail);
                if (count($pieces) > 1)
                {
                    for ($i = 0; $i < count($pieces); $i++) 
                    {
                        if($pieces[$i]!="")
                        {
                            $parts = explode("~", $pieces[$i]);
                          
                            // data[0] = obj.getString("nomor");
                            // data[1] = obj.getString("kodeBarang");
                            // data[2] = obj.getString("nomorBarang");
                            // data[3] = obj.getString("namaBarang");
                            // data[4] = obj.getString("nomorSatuan");
                            // data[5] = obj.getString("satuan");
                            // data[6] = obj.getString("jumlah");

                            //$tempHeader
                            $nomor_mhbarang = $parts[2];
                            $nomor_mhsatuan = $parts[4];
                            $jumlah = $parts[6];
                            $nomor_mhadmin = $nomorAdmin;

                            // ### query insert detail
                            $query = "INSERT INTO tdjualpraorder (
                                nomorthjualpraorder,
                                nomormhbarang,
                                nomormhsatuan,
                                nomormhsatuanunit,
                                jumlah,
                                jumlah_unit,
                                jumlah_konversi,
                                status_aktif,
                                dibuat_oleh,
                                dibuat_pada)
                                VALUES (
                                $tempNomorHeader,
                                $nomor_mhbarang,
                                $nomor_mhsatuan,
                                $nomor_mhsatuan,
                                $jumlah,
                                $jumlah,
                                1,
                                1,
                                $nomor_mhadmin,
                                '$todayDateTime'
                                )";

                            $this->db->query($query);
                        }
                    }
                }


            }


            if ($this->db->trans_status() === FALSE)
            {
                $this->db->trans_rollback();
                array_push($data['data'], array( 'query' => $this->error($query) ));
            }else{
                $this->db->trans_commit();
                array_push($data['data'], array( 'success' => 'true' ));
                        $this -> notifyNewPraorder($newKode);
            }

        }



        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }

    //Insert tambahan data baru, ketika header sudah terbentuk
    function insertItemPraorder_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### parameter dari andorid untuk item praorder
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");

        $nomorCabang = (isset($jsonObject["nomorCabang"]) ? $this->clean($jsonObject["nomorCabang"])     : ""); 
        $namaCabang = (isset($jsonObject["namaCabang"]) ? $this->clean($jsonObject["namaCabang"])     : ""); 
        $nomorCustomer = (isset($jsonObject["nomorCustomer"]) ? $this->clean($jsonObject["nomorCustomer"])     : "");
        $nomorSales = (isset($jsonObject["nomorSales"]) ? $this->clean($jsonObject["nomorSales"])     : "");
        $nomorJenisHarga = (isset($jsonObject["nomorJenisHarga"]) ? $this->clean($jsonObject["nomorJenisHarga"])     : "");
        $keterangan = (isset($jsonObject["keterangan"]) ? $this->clean($jsonObject["keterangan"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        if($nomorHeader > 0)
        {
            // ### berarti header sudah masuk database
            // ### do insert item detail

            // ### get data item dari adroid
            $noitem = FALSE;
            $dataitemdetail = (isset($jsonObject["dataitemdetail"]) ? $jsonObject["dataitemdetail"]     : "");

            // ### bongkar
            $pieces = explode('|', $dataitemdetail);
            if (count($pieces) > 1)
            {
                for ($i = 0; $i < count($pieces); $i++) 
                {
                    if($pieces[$i]!="")
                    {
                        $parts = explode("~", $pieces[$i]);
                      
                        // data[0] = obj.getString("nomor");
                        // data[1] = obj.getString("kodeBarang");
                        // data[2] = obj.getString("nomorBarang");
                        // data[3] = obj.getString("namaBarang");
                        // data[4] = obj.getString("nomorSatuan");
                        // data[5] = obj.getString("satuan");
                        // data[6] = obj.getString("jumlah");

                        //$tempHeader
                        $nomor_mhbarang = $parts[2];
                        $nomor_mhsatuan = $parts[4];
                        $jumlah = $parts[6];
                        $nomor_mhadmin = $nomorAdmin;

                        // ### query insert detail
                        $query = "INSERT INTO tdjualpraorder (
                            nomorthjualpraorder,
                            nomormhbarang,
                            nomormhsatuan,
                            nomormhsatuanunit,
                            jumlah,
                            jumlah_unit,
                            jumlah_konversi,
                            status_aktif,
                            dibuat_oleh,
                            dibuat_pada)
                            VALUES (
                            $nomorHeader,
                            $nomor_mhbarang,
                            $nomor_mhsatuan,
                            $nomor_mhsatuan,
                            $jumlah,
                            $jumlah,
                            1,
                            1,
                            $nomor_mhadmin,
                            '$todayDateTime'
                            )";

                        $this->db->query($query);
                    }
                }
            }


        }


        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }

    //## header praorder
    function updatePraorderHeader_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### list data yang diupdate
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");

        $nomorCabang = (isset($jsonObject["nomorCabang"]) ? $this->clean($jsonObject["nomorCabang"])     : "");  
        $nomorCustomer = (isset($jsonObject["nomorCustomer"]) ? $this->clean($jsonObject["nomorCustomer"])     : "");
        $nomorSales = (isset($jsonObject["nomorSales"]) ? $this->clean($jsonObject["nomorSales"])     : "");
        $nomorJenisHarga = (isset($jsonObject["nomorJenisHarga"]) ? $this->clean($jsonObject["nomorJenisHarga"])     : "");
        $tanggal = (isset($jsonObject["tanggal"]) ? $this->clean($jsonObject["tanggal"])     : "");
        $keterangan = (isset($jsonObject["keterangan"]) ? $this->clean($jsonObject["keterangan"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        $this->db->trans_begin();

        $query = "UPDATE thjualpraorder SET 
                nomormhcabang = $nomorCabang,
                nomormhcustomer = $nomorCustomer,
                nomormhsales = $nomorSales,
                nomormhjenisharga = $nomorJenisHarga,
                tanggal = '$tanggal',
                keterangan = '$keterangan',
                diubah_oleh = $nomorAdmin,
                diubah_pada = '$todayDateTime'
                WHERE nomor = $nomorHeader";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }

    function updatePraorderItem_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### list data yang diupdate
        $nomorItem = (isset($jsonObject["nomorItem"]) ? $this->clean($jsonObject["nomorItem"])     : "");

        $nomorBarang = (isset($jsonObject["nomorBarang"]) ? $this->clean($jsonObject["nomorBarang"])     : "");  
        $nomorSatuan = (isset($jsonObject["nomorSatuan"]) ? $this->clean($jsonObject["nomorSatuan"])     : "");
        $jumlah = (isset($jsonObject["jumlah"]) ? $this->clean($jsonObject["jumlah"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        $this->db->trans_begin();

        $query = "UPDATE tdjualpraorder SET 
                    nomormhbarang = $nomorBarang,
                    nomormhsatuan = $nomorSatuan,
                    nomormhsatuanunit = $nomorSatuan,
                    jumlah = $jumlah,
                    jumlah_unit = $jumlah,
                    diubah_oleh = $nomorAdmin,
                    diubah_pada = '$todayDateTime'
                    WHERE nomor =  $nomorItem";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }

    function deletePraorderItem_post()
    {
        // namanya delete tp sebenernya update status aktif dan dihapus oleh
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### list data yang diupdate
        $nomorItem = (isset($jsonObject["nomorItem"]) ? $this->clean($jsonObject["nomorItem"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        $this->db->trans_begin();

        $query = "UPDATE tdjualpraorder SET 
                    status_aktif = 0,
                    dihapus_oleh = $nomorAdmin,
                    dihapus_pada = '$todayDateTime'
                    WHERE nomor = $nomorItem";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function setApprovePraOrder_post(){
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        //thjualpraorder
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");
        // print_r($nomorHeader);
        // print_r($nomorAdmin);
        //$nomor = '253';

        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");


        $query = "SELECT status_disetujui FROM thjualpraorder WHERE nomor = $nomorHeader ";
        $result = $this->db->query($query);
        //echo $result->row()->status_disetujui;

        $approve = 1;
        $errormsg = '';
        //cek jika sudah diapprove atau belum
        if($result && $result->num_rows() > 0)
        {
            $approve = $result->row()->status_disetujui;
            //print_r($approve);
            if($approve == 0)
            {
                //jika belum diapprove maka ubah ke approve
                // mungkin nanti tambah filter klo di order jual sdh di approve apa blm
                $this->db->trans_begin();

                $query = "UPDATE thjualpraorder SET
                            status_disetujui = 1,
                            disetujui_oleh = $nomorAdmin,
                            disetujui_pada = '$todayDateTime'
                            WHERE nomor = $nomorHeader";

                $this->db->query($query);

                if ($this->db->trans_status() === FALSE)
                {
                    $this->db->trans_rollback();
                    array_push($data['data'], array( 'query' => $this->error($query) ));
                }else{
                    $this->db->trans_commit();
                    array_push($data['data'], array( 'success' => 'true' ));
                }

            }
            else
            {
                $errormsg = 'data has been already approved';
                array_push($data['data'], array( 'error' => $this->error($errormsg) ));
            }
        }
        else
        {
            $errormsg = 'data not found';
            array_push($data['data'], array( 'error' => $this->error($errormsg) ));
        }


        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function setDisapprovePraOrder_post(){
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        //thjualpraorder
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");
        // print_r($nomorHeader);
        // print_r($nomorAdmin);
        //$nomor = '253';

        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        // cek jika sudah di add di orderjual tidak bs di disapprove
        $queryCheck = "SELECT COUNT(*) as num FROM thjualorder WHERE nomorthjualpraorder = $nomorHeader LIMIT 1";
        $resultCheck = $this->db->query($queryCheck);
        $countOrderjual = $resultCheck->row()->num;

        if($countOrderjual == 0)
        {

            $query = "SELECT status_disetujui FROM thjualpraorder WHERE nomor = $nomorHeader ";
            $result = $this->db->query($query);
            //echo $result->row()->status_disetujui;

            $approve = 1;
            $errormsg = '';
            //cek jika sudah diapprove atau belum
            if($result && $result->num_rows() > 0)
            {
                $approve = $result->row()->status_disetujui;
                //print_r($approve);
                if($approve == 1)
                {
                    //jika kalau sudah di approve maka ubah ke disapprove
                    // mungkin nanti tambah filter klo di order jual sdh di approve apa blm
                    $this->db->trans_begin();

                    $query = "UPDATE thjualpraorder SET
                                status_disetujui = 0,
                                disetujui_oleh = $nomorAdmin,
                                disetujui_pada = '$todayDateTime'
                                WHERE nomor = $nomorHeader";

                    $this->db->query($query);

                    if ($this->db->trans_status() === FALSE)
                    {
                        $this->db->trans_rollback();
                        array_push($data['data'], array( 'query' => $this->error($query) ));
                    }else{
                        $this->db->trans_commit();
                        array_push($data['data'], array( 'success' => 'true' ));
                    }

                }
                else
                {
                    $errormsg = 'data status is Disapprove';
                    array_push($data['data'], array( 'error' => $this->error($errormsg) ));
                }
            }
            else
            {
                $errormsg = 'data not found';
                array_push($data['data'], array( 'error' => $this->error($errormsg) ));
            }
        }
        else
        {
            $errormsg = 'GAGAL karena Data sudah di add di Orderjual';
            array_push($data['data'], array( 'error' => $this->error($errormsg) ));
        }


        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    // fungsi gabungan untuk update praorder header dan itemmnya
    function updateAllPraorder_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### list data yang diupdate
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");

        $nomorCabang = (isset($jsonObject["nomorCabang"]) ? $this->clean($jsonObject["nomorCabang"])     : "");  
        $nomorCustomer = (isset($jsonObject["nomorCustomer"]) ? $this->clean($jsonObject["nomorCustomer"])     : "");
        $nomorSales = (isset($jsonObject["nomorSales"]) ? $this->clean($jsonObject["nomorSales"])     : "");
        $nomorJenisHarga = (isset($jsonObject["nomorJenisHarga"]) ? $this->clean($jsonObject["nomorJenisHarga"])     : "");
        $tanggal = (isset($jsonObject["tanggal"]) ? $this->clean($jsonObject["tanggal"])     : "");
        $keterangan = (isset($jsonObject["keterangan"]) ? $this->clean($jsonObject["keterangan"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        $this->db->trans_begin();

        $query = "UPDATE thjualpraorder SET 
                nomormhcabang = $nomorCabang,
                nomormhcustomer = $nomorCustomer,
                nomormhsales = $nomorSales,
                nomormhjenisharga = $nomorJenisHarga,
                tanggal = '$tanggal',
                keterangan = '$keterangan',
                diubah_oleh = $nomorAdmin,
                diubah_pada = '$todayDateTime'
                WHERE nomor = $nomorHeader";

        $this->db->query($query);

/// ################## update detail ##################
        $noitem = FALSE;
        $dataitemdetail = (isset($jsonObject["dataitemdetail"]) ? $jsonObject["dataitemdetail"]     : "");

        // ### bongkar
        $pieces = explode('|', $dataitemdetail);
        if (count($pieces) > 1)
        {
            for ($i = 0; $i < count($pieces); $i++) 
            {
                if($pieces[$i]!="")
                {
                    $parts = explode("~", $pieces[$i]);
                  
                        // data[0] = obj.getString("nomor");
                        // data[1] = obj.getString("kodeBarang");
                        // data[2] = obj.getString("nomorBarang");
                        // data[3] = obj.getString("namaBarang");
                        // data[4] = obj.getString("nomorSatuan");
                        // data[5] = obj.getString("satuan");
                        // data[6] = obj.getString("jumlah");
                        // data[7] = status 0 normal | 1 add, 2 edit, 3 delete

                    //$tempHeader
                    if($parts[7] == 1)
                    {
                        //add
                        $nomor_mhbarang_item = $parts[2];
                        $nomor_mhsatuan_item = $parts[4];
                        $jumlah_item = $parts[6];
                        $nomor_mhadmin_item = $nomorAdmin;

                        // ### query insert detail
                        $query = "INSERT INTO tdjualpraorder (
                            nomorthjualpraorder,
                            nomormhbarang,
                            nomormhsatuan,
                            nomormhsatuanunit,
                            jumlah,
                            jumlah_unit,
                            jumlah_konversi,
                            status_aktif,
                            dibuat_oleh,
                            dibuat_pada)
                            VALUES (
                            $nomorHeader,
                            $nomor_mhbarang_item,
                            $nomor_mhsatuan_item,
                            $nomor_mhsatuan_item,
                            $jumlah_item,
                            $jumlah_item,
                            1,
                            1,
                            $nomor_mhadmin_item,
                            '$todayDateTime'
                            )";

                        $this->db->query($query);
                    }
                    else if($parts[7] == 2)
                    {
                        $nomor_item = $parts[0];
                        $nomor_mhbarang_item = $parts[2];
                        $nomor_mhsatuan_item = $parts[4];
                        $jumlah_item = $parts[6];
                        $nomor_mhadmin_item = $nomorAdmin;

                        //edit
                        $query = "UPDATE tdjualpraorder SET 
                        nomormhbarang = $nomor_mhbarang_item,
                        nomormhsatuan = $nomor_mhsatuan_item,
                        nomormhsatuanunit = $nomor_mhsatuan_item,
                        jumlah = $jumlah_item,
                        jumlah_unit = $jumlah_item,
                        diubah_oleh = $nomor_mhadmin_item,
                        diubah_pada = '$todayDateTime'
                        WHERE nomor =  $nomor_item";

                        $this->db->query($query);
                    }
                    else if($parts[7] == 3)
                    {
                        //delete
                        $nomor_item = $parts[0];
                        $nomor_mhadmin_item = $nomorAdmin;

                        $query = "UPDATE tdjualpraorder SET 
                        status_aktif = 0,
                        dihapus_oleh = $nomor_mhadmin_item,
                        dihapus_pada = '$todayDateTime'
                        WHERE nomor = $nomor_item";

                        $this->db->query($query);
                    }

                }
            }
        }


        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    //vvv ####################################### ORDER JUAL ############################################## vvv

    function getOrderJualList_post(){
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $query = "SELECT
                    a.nomor,
                    a.kode, 
                    a.tanggal,
                    b.kode AS kodeCustomer,
                    b.nama AS namaCustomer,
                    a.keterangan,
                    a.status_disetujui
                    FROM thjualorder a
                    JOIN mhrelasi b ON a.nomormhcustomer = b.nomor
                    WHERE a.status_aktif = 1
                    ORDER BY a.nomor
                    #order by a.status_disetujui desc";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'tanggal'               => $r['tanggal'],
                                                'kodeCustomer'          => $r['kodeCustomer'],
                                                'namaCustomer'          => $r['namaCustomer'],
                                                'keterangan'            => $r['keterangan'],
                                                'status_disetujui'      => $r['status_disetujui']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    // jalan kalau item list di klik, kirim data yang lebih detail
    function getOrderJualDetilInfo_post(){
        $data['data'] = array();

        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");

        // note : ppn diskon,nominal value 0 semua
        $query = "SELECT
                    a.nomor,
                    a.kode,
                    a.nomormhcabang AS nomorCabang,
                    b.nama AS namaCabang,
                    b.kode AS kodeCabang,
                    a.nomormhcustomer AS nomorCustomer,
                    c.nama AS namaCustomer,
                    c.kode AS kodeCustomer,
                    a.nomormhvaluta AS nomorValuta,
                    d.kode AS kodeValuta,
                    d.simbol AS simbolValuta,
                    a.nomorthjualpraorder AS nomorPraorder,
                    e.kode AS kodePraorder,
                    a.tanggal,
                    a.kurs,
                    a.subtotal,
                    a.diskon_prosentase AS diskonPersen,
                    a.diskon_nominal AS diskonNominal,
                    a.ppn_prosentase AS ppnPersen,
                    a.ppn_nominal AS ppnNominal,
                    a.total,
                    a.totalrp,
                    a.keterangan,
                    a.status_disetujui,
                    f.nama as disetujui_oleh,
                    a.disetujui_pada
                    FROM thjualorder a
                    LEFT JOIN mhcabang b ON a.nomormhcabang = b.nomor
                    LEFT JOIN mhrelasi c ON a.nomormhcustomer = c.nomor
                    LEFT JOIN mhvaluta d ON a.nomormhvaluta = d.nomor
                    LEFT JOIN thjualpraorder e ON a.nomorthjualpraorder = e.nomor
                    LEFT JOIN mhadmin f on a.disetujui_oleh = f.nomor
                    WHERE a.status_aktif = 1
                    AND a.nomor = $nomorHeader";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                //26 data
                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],

                                                'nomorCabang'           => $r['nomorCabang'],
                                                'namaCabang'            => $r['namaCabang'],
                                                'kodeCabang'            => $r['kodeCabang'],

                                                'nomorCustomer'         => $r['nomorCustomer'],
                                                'namaCustomer'          => $r['namaCustomer'],
                                                'kodeCustomer'          => $r['kodeCustomer'],

                                                'nomorValuta'           => $r['nomorValuta'],
                                                'kodeValuta'            => $r['kodeValuta'],
                                                'simbolValuta'          => $r['simbolValuta'],

                                                'nomorPraorder'         => $r['nomorPraorder'],
                                                'kodePraorder'          => $r['kodePraorder'],

                                                'tanggal'               => $r['tanggal'],
                                                'kurs'                  => $r['kurs'],

                                                'subtotal'              => $r['subtotal'],

                                                'diskonPersen'          => $r['diskonPersen'],
                                                'diskonNominal'         => $r['diskonNominal'],
                                                'ppnPersen'             => $r['ppnPersen'],
                                                'ppnNominal'            => $r['ppnNominal'],

                                                'total'                 => $r['total'],
                                                'totalrp'               => $r['totalrp'],
                                                
                                                'keterangan'            => $r['keterangan'],
                                                'status_disetujui'      => $r['status_disetujui'],

                                                'disetujui_oleh'        => $r['disetujui_oleh'],
                                                'disetujui_pada'        => $r['disetujui_pada']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function getOrderJualItemList_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        //## nomor header dari thjualorder
        $nomor = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");  //input merupakan nomor thorderjual

        //show tdpora order item whre nomor header input user
        $todayDate = date("Y-m-d");

        $query = "SELECT
            a.nomor,
            b.nomor AS nomorBarang,
            b.kode AS kodeBarang,
            b.nama AS namaBarang,
            a.jumlah,
            c.nomor AS nomorSatuan,
            c.nama AS namaSatuan,
            a.harga,
            a.disc1 AS diskon,
            a.netto,
            a.subtotal,
            #Aktual
            #-order jual pakai 0
            #-nota jual pakai 1
            # nomorBarang,nomorGudang,Tgl,Aktual
            (SELECT cek_stok(b.nomor,0,'$todayDate',0)) AS 'stokTerkini'
            FROM tdjualorder a
            JOIN mhbarang b ON a.nomormhbarang = b.nomor
            JOIN mhsatuan c ON a.nomormhsatuan = c.nomor
            WHERE a.status_aktif = 1
            AND a.nomorthjualorder = $nomor";

        $result = $this->db->query($query);
        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){
                array_push($data['data'], array(
                                                'nomor'         => $r['nomor'],
                                                'nomorBarang'   => $r['nomorBarang'],
                                                'kodeBarang'    => $r['kodeBarang'],
                                                'namaBarang'    => $r['namaBarang'],
                                                'jumlah'        => $r['jumlah'],
                                                'nomorSatuan'   => $r['nomorSatuan'],
                                                'namaSatuan'    => $r['namaSatuan'],
                                                'harga'         => $r['harga'],
                                                'diskon'        => $r['diskon'],
                                                'netto'         => $r['netto'],
                                                'subtotal'      => $r['subtotal'],
                                                'stokTerkini'   => $r['stokTerkini']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    function getPraorderHeaderbyCustomer_post(){
        // ### untuk mendapatkan list thpraorder where no customer tertentu
        // ### hanya untuk menspesifickan pilihan praorder sebelum di olah di orderjual
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $nomorCustomer = (isset($jsonObject["nomorCustomer"]) ? $this->clean($jsonObject["nomorCustomer"])     : ""); 

        $query = "SELECT
                    a.nomor,
                    a.kode,
                    a.tanggal,
                    b.kode as kodeCustomer,
                    b.nama as namaCustomer,
                    a.keterangan,
                    a.status_disetujui
                    from thjualpraorder a
                    join mhrelasi b
                    on a.nomormhcustomer = b.nomor
                    where a.status_aktif = 1
                    AND a.status_disetujui = 1
                    AND b.nomor = $nomorCustomer";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'                 => $r['nomor'],
                                                'kode'                  => $r['kode'],
                                                'tanggal'               => $r['tanggal'],
                                                'kodeCustomer'          => $r['kodeCustomer'],
                                                'namaCustomer'          => $r['namaCustomer'],
                                                'keterangan'            => $r['keterangan'],
                                                'status_disetujui'      => $r['status_disetujui']
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function getOrderJualItemFromPraorder_post(){
        // ### untuk mendapatkan list item sesuai dengan item yang ada di praorder
        // ### yang nantinya akan di olah di order jual
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        // ### param berupa nomor thpraorder
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");
        $todayDate = date("Y-m-d"); 

        $query = "SELECT 
                    a.nomor,
                    b.nomor AS nomorBarang,
                    b.kode AS kodeBarang,
                    b.nama AS namaBarang,
                    a.jumlah,
                    d.nomor AS nomorSatuan,
                    d.nama AS namaSatuan,
                    #e.nomor AS nomorJenisHarga,
                    #e.nama AS namaJenisHarga,
                    @harga:=(SELECT DISTINCT(harga) FROM mdbarangharga WHERE nomormhbarang = b.nomor AND nomormhjenisharga = c.nomormhjenisharga) AS harga,
                    a.disc1 AS diskon,
                    @netto:= @harga - (a.disc1 * @harga) AS netto,
                    a.jumlah * @netto AS subtotal,
                    ( SELECT cek_stok(a.nomor,0,'$todayDate',0) ) AS stokTerkini
                    FROM tdjualpraorder a
                    JOIN mhbarang b ON a.nomormhbarang = b.nomor
                    JOIN thjualpraorder c ON a.nomorthjualpraorder = c.nomor
                    JOIN mhsatuan d ON a.nomormhsatuan = d.nomor
                    JOIN mhjenisharga e ON c.nomormhjenisharga = e.nomor
                    WHERE c.nomor= $nomorHeader";

        $result = $this->db->query($query);

        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($data['data'], array(
                                                'nomor'             => $r['nomor'],
                                                'nomorBarang'       => $r['nomorBarang'],
                                                'kodeBarang'        => $r['kodeBarang'],
                                                'namaBarang'        => $r['namaBarang'],
                                                'jumlah'            => $r['jumlah'],
                                                'nomorSatuan'       => $r['nomorSatuan'],
                                                'namaSatuan'        => $r['namaSatuan'],
                                                //'nomorJenisHarga'   => $r['nomorJenisHarga'],
                                                //'namaJenisHarga'    => $r['namaJenisHarga'],
                                                'harga'             => $r['harga'],
                                                'diskon'            => $r['diskon'],
                                                'netto'             => $r['netto'],
                                                'subtotal'          => $r['subtotal'],
                                                'stokTerkini'       => $r['stokTerkini']                                              
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function createNewOrderjualKode($namaCabang)
    {
        $newKode = "OJ";
        $tgl = substr(date("Ym"),2);
        $query = "SELECT a.kode,a.tanggal 
                    FROM thjualorder a
                    #where a.status_aktif = 1
                    ORDER BY nomor DESC 
                    LIMIT 2";

        $tempKode['data'] = array();
        $result = $this->db->query($query);
        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($tempKode['data'], array(
                                                'kode'             => $r['kode'],
                                                'tanggal'          => $r['tanggal']                                           
                                                )
                );
            }
        }else{
            array_push($tempKode['data'], array( 'query' => $this->error($query) ));
        }

        $tempDate1 = (int)substr(str_replace("-","", $tempKode['data'][0][tanggal]),0,6);  //echo $tempDate1."";
        $tempDate2 = (int)substr(str_replace("-","", $tempKode['data'][1][tanggal]),0,6);  //echo $tempDate2."";

        // ### pengecekan reset counter untuk ganti bulan
        if($tempDate1 > $tempDate2)
        {
            $strKode = 0; // reset
        }
        else
        {
            $strKode = substr($tempKode['data'][0][kode], -4); // ambil 4 dari belakang
        }

        $counter = (int)$strKode+1; // increment
        //kembalikan ke format angka semula
        $strCounter = (string)$counter;
        if(strlen($strCounter) == 1)
        {
            $strCounter = "000".$strCounter;
        }
        else if(strlen($strCounter) == 2)
        {
            $strCounter = "00".$strCounter;
        }
        else if(strlen($strCounter) == 3)
        {
            $strCounter = "0".$strCounter;
        }
        //gabungin semua
        $newKode = $newKode."/".$kodeCabang."/".$tgl."/".$strCounter;

        return $newKode;

    }

    //### insert orderjual
    function insertOrderjual_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### parameter dari andorid untuk header
        $nomorCabang = (isset($jsonObject["nomorCabang"]) ? $this->clean($jsonObject["nomorCabang"])     : "");
        $kodeCabang =  (isset($jsonObject["kodeCabang"]) ? $this->clean($jsonObject["kodeCabang"])     : "");
        $nomorCustomer = (isset($jsonObject["nomorCustomer"]) ? $this->clean($jsonObject["nomorCustomer"])     : "");
        $nomorValuta = (isset($jsonObject["nomorValuta"]) ? $this->clean($jsonObject["nomorValuta"])     : "");
        $nomorPraorder = (isset($jsonObject["nomorPraorder"]) ? $this->clean($jsonObject["nomorPraorder"])     : ""); 
        $kurs = (isset($jsonObject["kurs"]) ? $this->clean($jsonObject["kurs"])     : "");
        $subtotal = (isset($jsonObject["subtotal"]) ? $this->clean($jsonObject["subtotal"])     : "");
        $total = (isset($jsonObject["total"]) ? $this->clean($jsonObject["total"])     : "");
        $totalrp = (isset($jsonObject["totalrp"]) ? $this->clean($jsonObject["totalrp"])     : "");
        $ppnPersen = (isset($jsonObject["ppnPersen"]) ? $this->clean($jsonObject["ppnPersen"])     : "");
        $ppnNominal = (isset($jsonObject["ppnNominal"]) ? $this->clean($jsonObject["ppnNominal"])     : "");
        $diskonPersen = (isset($jsonObject["diskonPersen"]) ? $this->clean($jsonObject["diskonPersen"])     : "");
        $diskonNominal = (isset($jsonObject["diskonNominal"]) ? $this->clean($jsonObject["diskonNominal"])     : "");
        $tanggal =  (isset($jsonObject["tanggal"]) ? $this->clean($jsonObject["tanggal"])     : "");
        $keterangan = (isset($jsonObject["keterangan"]) ? $this->clean($jsonObject["keterangan"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");



// ### create new kode untuk header
        $newKode = $this -> createNewOrderjualKode($namaCabang);

// ### query insert untuk header
        //### mhsales = 0, mhgudang  = 0;
        $this->db->trans_begin();
        $query = "INSERT INTO thjualorder(
                    nomormhcabang,
                    nomormhcustomer,
                    nomormhsales,
                    nomormhvaluta,
                    nomormhgudang,
                    nomorthjualpraorder,
                    kode,
                    tanggal,
                    kurs,
                    subtotal,
                    diskon_prosentase,
                    diskon_nominal,
                    ppn_prosentase,
                    ppn_nominal,
                    total,
                    totalrp,
                    keterangan,
                    status_aktif,
                    dibuat_oleh,
                    dibuat_pada,
                    status_disetujui,
                    disetujui_oleh
                    )
                    VALUES (
                    $nomorCabang,
                    $nomorCustomer,
                    0,
                    $nomorValuta,
                    0,
                    $nomorPraorder,
                    '$newKode',
                    '$tanggal',
                    $kurs,
                    $subtotal,
                    $diskonPersen,
                    $diskonNominal,
                    $ppnPersen,
                    $ppnNominal,
                    $total,
                    $totalrp,
                    '$keterangan',
                    1,
                    $nomorAdmin,
                    '$todayDateTime',
                    0,
                    0
                    )";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            //array_push($data['data'], array( 'success' => 'true' ));


            $query = " SELECT nomor AS nomorHeader FROM thjualorder
            WHERE status_aktif = 1
            AND kode = '$newKode' ";

            $tempNomorHeader;

            $result = $this->db->query($query);
            if( $result && $result->num_rows() > 0){
                foreach ($result->result_array() as $r){
                    $tempNomorHeader = $r['nomorHeader'];
                }
            }else{
                array_push($data['data'], array( 'query' => $this->error($query) ));
            }

            if($tempNomorHeader > 0)
            {
                // ### berarti header sudah masuk database
                // ### do insert item detail

                // ### get data item dari adroid
                $noitem = FALSE;
                $dataitemdetail = (isset($jsonObject["dataitemdetail"]) ? $jsonObject["dataitemdetail"]     : "");

                // ### bongkar
                $pieces = explode('|', $dataitemdetail);
                if (count($pieces) > 1)
                {
                    for ($i = 0; $i < count($pieces); $i++) 
                    {
                        if($pieces[$i]!="")
                        {
                            $parts = explode("~", $pieces[$i]);
                          
                            // data[0] = obj.getString("nomor");
                            // data[1] = obj.getString("nomorBarang");
                            // data[2] = obj.getString("kodeBarang");
                            // data[3] = obj.getString("namaBarang");
                            // data[4] = obj.getString("jumlah");
                            // data[5] = obj.getString("nomorSatuan");
                            // data[6] = obj.getString("namaSatuan");
                            // data[7] = obj.getString("harga");
                            // data[8] = obj.getString("diskon");
                            // data[9] = obj.getString("netto");
                            // data[10] = obj.getString("subtotal");
                            // data[11] = obj.getString("stokTerkini");

                            //$tempHeader
                            $nomor_praorder = $parts[0];
                            $nomor_mhbarang = $parts[1];
                            $nomor_mhsatuan = $parts[5];
                            $jumlah_item = $parts[4];
                            $harga_item = $parts[7];
                            $diskon_item = $parts[8];
                            $netto_item = $parts[9];
                            $subtotal_item = $parts[10];
                            $nomor_mhadmin = $nomorAdmin;

                            // ### query insert detail
                            $query = "INSERT INTO tdjualorder (
                                        nomorthjualorder,
                                        nomortdjualpraorder,
                                        nomormhbarang,
                                        nomormhsatuan,
                                        nomormhsatuanunit,
                                        jumlah,
                                        jumlah_unit,
                                        jumlah_konversi,
                                        harga,
                                        disc1,
                                        netto,
                                        subtotal,
                                        status_aktif,
                                        dibuat_oleh,
                                        dibuat_pada)
                                        VALUES (
                                        $tempNomorHeader,
                                        $nomor_praorder,
                                        $nomor_mhbarang,
                                        $nomor_mhsatuan,
                                        $nomor_mhsatuan,
                                        $jumlah_item,
                                        $jumlah_item,
                                        1,
                                        $harga_item,
                                        $diskon_item,
                                        $netto_item,
                                        $subtotal_item,
                                        1,
                                        $nomor_mhadmin,
                                        '$todayDateTime'
                                        )";

                            $this->db->query($query);
                        }
                    }
                }


            }


            if ($this->db->trans_status() === FALSE)
            {
                $this->db->trans_rollback();
                array_push($data['data'], array( 'query' => $this->error($query) ));
            }else{
                $this->db->trans_commit();
                array_push($data['data'], array( 'success' => 'true' ));
                $this -> notifyNewOrderjual($newKode);
            }

        }


        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }

    function deleteOrderJualItem_post()
    {
        // namanya delete tp sebenernya update status aktif dan dihapus oleh
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### list data yang diupdate
        $nomorItem = (isset($jsonObject["nomorItem"]) ? $this->clean($jsonObject["nomorItem"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        $this->db->trans_begin();

        $query = "UPDATE tdjualorder SET 
                    status_aktif = 0,
                    dihapus_oleh = $nomorAdmin,
                    dihapus_pada = '$todayDateTime'
                    WHERE nomor = $nomorItem";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    function updateOrderJualItem_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### list data yang diupdate
        $nomorItem = (isset($jsonObject["nomorItem"]) ? $this->clean($jsonObject["nomorItem"])     : "");

        $nomorBarang = (isset($jsonObject["nomorBarang"]) ? $this->clean($jsonObject["nomorBarang"])     : "");  
        $jumlah = (isset($jsonObject["jumlah"]) ? $this->clean($jsonObject["jumlah"])     : "");
        $harga = (isset($jsonObject["harga"]) ? $this->clean($jsonObject["harga"])     : "");
        $diskon = (isset($jsonObject["diskon"]) ? $this->clean($jsonObject["diskon"])     : "");
        $netto = (isset($jsonObject["netto"]) ? $this->clean($jsonObject["netto"])     : "");
        $subtotal = (isset($jsonObject["subtotal"]) ? $this->clean($jsonObject["subtotal"])     : "");

        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        $this->db->trans_begin();

        $query = "UPDATE tdjualorder SET 
                    nomormhbarang = $nomorBarang,
                    jumlah = $jumlah,
                    jumlah_unit = $jumlah,
                    harga = $harga,
                    disc1 = $diskon,
                    netto = $netto,
                    subtotal = $subtotal,
                    diubah_oleh = $nomorAdmin,
                    diubah_pada = '$todayDateTime'
                    WHERE nomor =$nomorItem";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }

    }

    function updateOrderJualHeader_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### list data yang diupdate
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");

        $nomorCabang = (isset($jsonObject["nomorCabang"]) ? $this->clean($jsonObject["nomorCabang"])     : "");
        $tanggal =  (isset($jsonObject["tanggal"]) ? $this->clean($jsonObject["tanggal"])     : "");
        $nomorCustomer = (isset($jsonObject["nomorCustomer"]) ? $this->clean($jsonObject["nomorCustomer"])     : "");
        $nomorValuta = (isset($jsonObject["nomorValuta"]) ? $this->clean($jsonObject["nomorValuta"])     : "");
        $nomorPraorder = (isset($jsonObject["nomorPraorder"]) ? $this->clean($jsonObject["nomorPraorder"])     : ""); 
        $kurs = (isset($jsonObject["kurs"]) ? $this->clean($jsonObject["kurs"])     : "");
        $subtotal = (isset($jsonObject["subtotal"]) ? $this->clean($jsonObject["subtotal"])     : "");
        $total = (isset($jsonObject["total"]) ? $this->clean($jsonObject["total"])     : "");
        $totalrp = (isset($jsonObject["totalrp"]) ? $this->clean($jsonObject["totalrp"])     : "");
        $ppnPersen = (isset($jsonObject["ppnPersen"]) ? $this->clean($jsonObject["ppnPersen"])     : "");
        $ppnNominal = (isset($jsonObject["ppnNominal"]) ? $this->clean($jsonObject["ppnNominal"])     : "");
        $diskonPersen = (isset($jsonObject["diskonPersen"]) ? $this->clean($jsonObject["diskonPersen"])     : "");
        $diskonNominal = (isset($jsonObject["diskonNominal"]) ? $this->clean($jsonObject["diskonNominal"])     : "");

        $keterangan = (isset($jsonObject["keterangan"]) ? $this->clean($jsonObject["keterangan"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        $this->db->trans_begin();

        $query = "UPDATE thjualorder SET 
                    nomormhcabang = $nomorCabang,
                    nomormhcustomer = $nomorCustomer,
                    nomormhvaluta = $nomorValuta,
                    nomorthjualpraorder = $nomorPraorder,
                    tanggal = '$tanggal',
                    kurs = $kurs,
                    subtotal = $subtotal,
                    diskon_prosentase = $diskonPersen,
                    diskon_nominal = $diskonNominal,
                    ppn_prosentase = $ppnPersen,
                    ppn_nominal = $ppnNominal,
                    total = $total,
                    totalrp = $totalrp,
                    keterangan = '$keterangan',
                    diubah_oleh = $nomorAdmin,
                    diubah_pada = '$todayDateTime'
                    WHERE nomor =  $nomorHeader";

        $this->db->query($query);

        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    function setApproveOrderjual_post(){
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        //thjualpraorder
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");
        // print_r($nomorHeader);
        // print_r($nomorAdmin);
        //$nomor = '253';

        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");


        $query = "SELECT status_disetujui FROM thjualorder WHERE nomor = $nomorHeader ";
        $result = $this->db->query($query);
        //echo $result->row()->status_disetujui;

        $approve = 1;
        $errormsg = '';
        //cek jika sudah diapprove atau belum
        if($result && $result->num_rows() > 0)
        {
            $approve = $result->row()->status_disetujui;
            //print_r($approve);
            if($approve == 0)
            {
                //jika belum diapprove maka ubah ke approve
                // mungkin nanti tambah filter klo di order jual sdh di approve apa blm
                $this->db->trans_begin();

                $query = "UPDATE thjualorder SET
                            status_disetujui = 1,
                            disetujui_oleh = $nomorAdmin,
                            disetujui_pada = '$todayDateTime'
                            WHERE nomor = $nomorHeader";

                $this->db->query($query);

                if ($this->db->trans_status() === FALSE)
                {
                    $this->db->trans_rollback();
                    array_push($data['data'], array( 'query' => $this->error($query) ));
                }else{
                    $this->db->trans_commit();
                    array_push($data['data'], array( 'success' => 'true' ));
                }

            }
            else
            {
                $errormsg = 'data has been already approved';
                array_push($data['data'], array( 'error' => $this->error($errormsg) ));
            }
        }
        else
        {
            $errormsg = 'data not found';
            array_push($data['data'], array( 'error' => $this->error($errormsg) ));
        }


        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function setDisapproveOrderjual_post(){
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        //thjualpraorder
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");
        // print_r($nomorHeader);
        // print_r($nomorAdmin);
        //$nomor = '253';

        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");


        $query = "SELECT status_disetujui FROM thjualorder WHERE nomor = $nomorHeader ";
        $result = $this->db->query($query);
        //echo $result->row()->status_disetujui;

        $approve = 1;
        $errormsg = '';
        //cek jika sudah diapprove atau belum
        if($result && $result->num_rows() > 0)
        {
            $approve = $result->row()->status_disetujui;
            //print_r($approve);
            if($approve == 1)
            {
                //jika kalau sudah di approve maka ubah ke disapprove
                // mungkin nanti tambah filter klo di order jual sdh di approve apa blm
                $this->db->trans_begin();

                $query = "UPDATE thjualorder SET
                            status_disetujui = 0,
                            disetujui_oleh = $nomorAdmin,
                            disetujui_pada = '$todayDateTime'
                            WHERE nomor = $nomorHeader";

                $this->db->query($query);

                if ($this->db->trans_status() === FALSE)
                {
                    $this->db->trans_rollback();
                    array_push($data['data'], array( 'query' => $this->error($query) ));
                }else{
                    $this->db->trans_commit();
                    array_push($data['data'], array( 'success' => 'true' ));
                }

            }
            else
            {
                $errormsg = 'data status is Disapprove';
                array_push($data['data'], array( 'error' => $this->error($errormsg) ));
            }
        }
        else
        {
            $errormsg = 'data not found';
            array_push($data['data'], array( 'error' => $this->error($errormsg) ));
        }


        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }


    // fungsi gabungan untuk update orderjual header dan itemmnya
    function updateEditDeleteAllOrderJual_post()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

// ### list data yang diupdate
        $nomorHeader = (isset($jsonObject["nomorHeader"]) ? $this->clean($jsonObject["nomorHeader"])     : "");

        $nomorCabang = (isset($jsonObject["nomorCabang"]) ? $this->clean($jsonObject["nomorCabang"])     : "");
        $tanggal =  (isset($jsonObject["tanggal"]) ? $this->clean($jsonObject["tanggal"])     : "");
        $nomorCustomer = (isset($jsonObject["nomorCustomer"]) ? $this->clean($jsonObject["nomorCustomer"])     : "");
        $nomorValuta = (isset($jsonObject["nomorValuta"]) ? $this->clean($jsonObject["nomorValuta"])     : "");
        $nomorPraorder = (isset($jsonObject["nomorPraorder"]) ? $this->clean($jsonObject["nomorPraorder"])     : ""); 
        $kurs = (isset($jsonObject["kurs"]) ? $this->clean($jsonObject["kurs"])     : "");
        $subtotal = (isset($jsonObject["subtotal"]) ? $this->clean($jsonObject["subtotal"])     : "");
        $total = (isset($jsonObject["total"]) ? $this->clean($jsonObject["total"])     : "");
        $totalrp = (isset($jsonObject["totalrp"]) ? $this->clean($jsonObject["totalrp"])     : "");
        $ppnPersen = (isset($jsonObject["ppnPersen"]) ? $this->clean($jsonObject["ppnPersen"])     : "");
        $ppnNominal = (isset($jsonObject["ppnNominal"]) ? $this->clean($jsonObject["ppnNominal"])     : "");
        $diskonPersen = (isset($jsonObject["diskonPersen"]) ? $this->clean($jsonObject["diskonPersen"])     : "");
        $diskonNominal = (isset($jsonObject["diskonNominal"]) ? $this->clean($jsonObject["diskonNominal"])     : "");

        $keterangan = (isset($jsonObject["keterangan"]) ? $this->clean($jsonObject["keterangan"])     : "");
        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "");

// ### parameter waktu untuk db
        $todayDate = date("Y-m-d");
        $todayDateTime = date("Y-m-d h:i:s");

        $this->db->trans_begin();

        $query = "UPDATE thjualorder SET 
                    nomormhcabang = $nomorCabang,
                    nomormhcustomer = $nomorCustomer,
                    nomormhvaluta = $nomorValuta,
                    nomorthjualpraorder = $nomorPraorder,
                    tanggal = '$tanggal',
                    kurs = $kurs,
                    subtotal = $subtotal,
                    diskon_prosentase = $diskonPersen,
                    diskon_nominal = $diskonNominal,
                    ppn_prosentase = $ppnPersen,
                    ppn_nominal = $ppnNominal,
                    total = $total,
                    totalrp = $totalrp,
                    keterangan = '$keterangan',
                    diubah_oleh = $nomorAdmin,
                    diubah_pada = '$todayDateTime'
                    WHERE nomor =  $nomorHeader";

        $this->db->query($query);

/// ################## udapte detail ##################
        $noitem = FALSE;
        $dataitemdetail = (isset($jsonObject["dataitemdetail"]) ? $jsonObject["dataitemdetail"]     : "");

        // ### bongkar
        $pieces = explode('|', $dataitemdetail);
        if (count($pieces) > 1)
        {
            for ($i = 0; $i < count($pieces); $i++) 
            {
                if($pieces[$i]!="")
                {
                    $parts = explode("~", $pieces[$i]);
                  
                            // data[0] = obj.getString("nomor");
                            // data[1] = obj.getString("nomorBarang");
                            // data[2] = obj.getString("kodeBarang");
                            // data[3] = obj.getString("namaBarang");
                            // data[4] = obj.getString("jumlah");
                            // data[5] = obj.getString("nomorSatuan");
                            // data[6] = obj.getString("namaSatuan");
                            // data[7] = obj.getString("harga");
                            // data[8] = obj.getString("diskon");
                            // data[9] = obj.getString("netto");
                            // data[10] = obj.getString("subtotal");
                            // data[11] = obj.getString("stokTerkini");
                            // data[12] = status 0 normal, 1 delete, 2 edit

                    //$tempHeader
                    if($parts[12] == 2)
                    {
                        //edit
                        $nomor_item = $parts[0];
                        $nomor_mhbarang = $parts[1];
                        $nomor_mhsatuan = $parts[5];
                        $jumlah_item = $parts[4];
                        $harga_item = $parts[7];
                        $diskon_item = $parts[8];
                        $netto_item = $parts[9];
                        $subtotal_item = $parts[10];
                        $nomor_mhadmin = $nomorAdmin;

                        // ### query update detail
                        $query = "UPDATE tdjualorder SET 
                                    nomormhbarang = $nomor_mhbarang,
                                    jumlah = $jumlah_item,
                                    jumlah_unit = $jumlah_item,
                                    harga = $harga_item,
                                    disc1 = $diskon_item,
                                    netto = $netto_item,
                                    subtotal = $subtotal_item,
                                    diubah_oleh = $nomorAdmin,
                                    diubah_pada = '$todayDateTime'
                                    WHERE nomor = $nomor_item";
                    }
                    else if($parts[12] == 1)
                    {
                        //delete
                        $nomor_item = $parts[0];
                        $query = "UPDATE tdjualorder SET 
                                status_aktif = 0,
                                dihapus_oleh = $nomorAdmin,
                                dihapus_pada = '$todayDateTime'
                                WHERE nomor = $nomor_item";
                    }

                    $this->db->query($query);
                }
            }
        }


        if ($this->db->trans_status() === FALSE)
        {
            $this->db->trans_rollback();
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }else{
            $this->db->trans_commit();
            array_push($data['data'], array( 'success' => 'true' ));
        }

        if ($data){
            // Set the response and exit
            $this->response($data['data']); // OK (200) being the HTTP response code
        }
    }

    function getKode()
    {
        $kodeCabang = "SBY";
        $newKode = "OJ";
        $tgl = substr(date("Ym"),2);
        $query = "SELECT a.kode,a.tanggal 
                    FROM thjualorder a
                    #where a.status_aktif = 1
                    ORDER BY nomor DESC 
                    LIMIT 2";

        $tempKode['data'] = array();
        $result = $this->db->query($query);
        if( $result && $result->num_rows() > 0){
            foreach ($result->result_array() as $r){

                array_push($tempKode['data'], array(
                                                'kode'             => $r['kode'],
                                                'tanggal'          => $r['tanggal']                                           
                                                )
                );
            }
        }else{
            array_push($data['data'], array( 'query' => $this->error($query) ));
        }

        $tempDate1 = (int)substr(str_replace("-","", $tempKode['data'][0][tanggal]),0,6);  //echo $tempDate1."";
        $tempDate2 = (int)substr(str_replace("-","", $tempKode['data'][1][tanggal]),0,6);  //echo $tempDate2."";

        // ### pengecekan reset counter untuk ganti bulan
        if($tempDate1 > $tempDate2)
        {
            //echo "lalla";
            $strKode = 0; // reset
        }
        else
        {
            //echo substr($tempKode['data'][0][kode], -4);
            $strKode = substr($tempKode['data'][0][kode], -4); // ambil 4 dari belakang
        }

        $counter = (int)$strKode+1; // increment
        //kembalikan ke format angka semula
        $strCounter = (string)$counter;
        if(strlen($strCounter) == 1)
        {
            $strCounter = "0000".$strCounter;
        }
        else if(strlen($strCounter) == 2)
        {
            $strCounter = "000".$strCounter;
        }
        else if(strlen($strCounter) == 3)
        {
            $strCounter = "00".$strCounter;
        }
        //gabungin semua
        $newKode = $newKode."/".$kodeCabang."/".$tgl."/".$strCounter;

        print_r($newKode);
    }



//testing aja
    function getTest_get()
    {
        $data['data'] = array();
        $value = file_get_contents('php://input');
        $jsonObject = (json_decode($value , true));

        $nomorAdmin = (isset($jsonObject["nomorAdmin"]) ? $this->clean($jsonObject["nomorAdmin"])     : "100");

        $this->getKode();

        //$nomor = (isset($jsonObject["limit"]) ? $this->clean($jsonObject["limit"])     : "");  //input merupakan nomor thorderjual

        //show tdpora order item whre nomor header input user

        // $query = "SELECT nomor,kode from thjualpraorder 
        //             where status_aktif = 1 
        //             limit 100";

        // $result = $this->db->query($query);
        // if( $result && $result->num_rows() > 0){
        //     foreach ($result->result_array() as $r){
        //         array_push($data['data'], array(
        //                                         'nomor'         => $r['nomor'],
        //                                         'kode'         => $r['kode']
        //                                         )
        //         );
        //     }
        // }else{
        //     array_push($data['data'], array( 'query' => $this->error($query) ));
        // }

        // if ($data){
        //     // Set the response and exit
        //     $this->response($data['data']); // OK (200) being the HTTP response code
        // }
    }






	//untuk menampilkan data di salesorder list
	// function getSalesOrderList_post(){
	// 	$data['data'] = array();

 //        $value = file_get_contents('php://input');
 //        $jsonObject = (json_decode($value , true));
	// 	$nomorsales = (isset($jsonObject["nomorsales"]) ? $this->clean($jsonObject["nomorsales"])     : "");
	// 	$approve = (isset($jsonObject["approve"]) ? $this->clean($jsonObject["approve"])     : "0");
	// 	$kode = (isset($jsonObject["kode"]) ? $jsonObject["kode"]     : "");
	// 	$cabang = (isset($jsonObject["cabang"]) ? $this->clean($jsonObject["cabang"])     : "");

 //        if($nomorsales!="") $nomorsales = " AND a.nomorsales = '" . $nomorsales . "'";

 //        $query = "SELECT
 //                  a.Nomor nomor,
 //                  a.Kode kode,
 //                  a.Tanggal tanggal,
 //                  a.NomorCabang nomorcabang,
 //                  a.Cabang cabang,
 //                  a.NomorCustomer nomorcustomer,
 //                  a.KodeCustomer kodecustomer,
 //                  b.nama namacustomer
 //                  FROM thorderjual a
 //                  JOIN tcustomer b
 //                    ON b.nomor = a.nomorcustomer
 //                  WHERE a.status = 1
 //                    $nomorsales
 //                    AND a.approve = $approve
 //                    AND a.NomorCabang = $cabang
 //                    AND a.kode REGEXP '$kode'";

 //        $result = $this->db->query($query);

 //        if( $result && $result->num_rows() > 0){
 //            foreach ($result->result_array() as $r){

 //                array_push($data['data'], array(
 //                                                'nomor'					=> $r['nomor'],
 //                                                'kode'					=> $r['kode'],
 //                                                'tanggal' 			    => $r['tanggal'],
 //                                                'nomorcabang' 			=> $r['nomorcabang'],
 //                                                'cabang' 			    => $r['cabang'],
 //                                                'nomorcustomer' 	    => $r['nomorcustomer'],
 //                                                'kodecustomer' 			=> $r['kodecustomer'],
 //                                                'namacustomer' 			=> $r['namacustomer']
 //                                                )
 //                );
 //            }
 //        }else{
 //            array_push($data['data'], array( 'query' => $this->error($query) ));
 //        }

 //        if ($data){
 //            // Set the response and exit
 //            $this->response($data['data']); // OK (200) being the HTTP response code
 //        }
	// }

//     //added by Tonny
//     // untuk mendapatkan format setting untuk sales order
//     //output -> [prefix],[length nomor urut],[format bulan tahun],[header transaksi],[detail transaksi] -> 'SOP,5,YYMM,TTRANSAKSI,TDORDERJUAL'
//     function getFormatSettingSalesOrder_post(){
//         $data['data'] = array();

//         $value = file_get_contents('php://input');
//         $jsonObject = (json_decode($value , true));
//         $nomor = (isset($jsonObject["nomor"]) ? $this->clean($jsonObject["nomor"])     : "219");
//         $query = "SELECT formatsetting FROM tformatsetting WHERE nomor = $nomor";
//         $result = $this->db->query($query);
//         if($result && $result->num_rows() > 0){
//             foreach ($result->result_array() as $r){
//                 array_push($data['data'], array(
//                                                 'formatsetting'			=> $r['formatsetting']
//                                                 )
//                 );
//             }
//         }else{
//             array_push($data['data'], array( 'query' => $this->error($query) ));
//         }

//         if ($data){
//             // Set the response and exit
//             $this->response($data['data']); // OK (200) being the HTTP response code
//         }
//     }

//     //added by Tonny
//     //untuk mendapatkan nomor baru untuk salesorder header
//     function getCounterHeader_post(){
//         $data['data'] = array();

//         $value = file_get_contents('php://input');
//         $jsonObject = (json_decode($value , true));
//         $prefix = (isset($jsonObject["prefix"]) ? $this->clean($jsonObject["prefix"])     : "SOP");  //input merupakan kodetransaksi
//         //$query = "SELECT MAX(SUBSTR(kode, LOCATE('/', kode, 8) + 1)) AS counter FROM thorderjual WHERE kode LIKE '%$prefix%'";
//         $query = "SELECT MAX(SUBSTR(kode, LOCATE('/', kode, LOCATE('/', kode) + 1) + 1)) AS counter FROM thorderjual WHERE kode LIKE '%SOP%'";
//         $result = $this->db->query($query);
//         if( $result && $result->num_rows() > 0){
//             foreach ($result->result_array() as $r){
//                 array_push($data['data'], array(
//                                                 'counter'	=> $r['counter']
//                                                 )
//                 );
//             }
//         }else{
//             array_push($data['data'], array( 'query' => $this->error($query) ));
//         }

//         if ($data){
//             // Set the response and exit
//             $this->response($data['data']); // OK (200) being the HTTP response code
//         }
//     }

//     //added by Tonny
//     //untuk mendapatkan nomor baru untuk salesorder detail
//     function getNomorHeader($_headerkode){
//         $result = $this->db->query("SELECT akhir counter FROM tcount WHERE kode = '$_headerkode'")->row()->counter;
//         return $result + 1;
//     }

//     //added by Tonny
//     //untuk mendapatkan nomor baru untuk salesorder detail
//     function getNomorDetail($_detailkode){
//         $result = $this->db->query("SELECT akhir counter FROM tcount WHERE kode = '$_detailkode'")->row()->counter;
//         return $result + 1;
//     }

//     //untuk testing saja
//     function getHeader_get(){
//         $this->response($this->getNomorHeader('ttransaksi'));
//     }

//     //added by Tonny
//     //untuk mendapatkan list item berdasarkan nomor thorderjual
//     function getSalesOrderItemList_post(){
//         $data['data'] = array();
//         $value = file_get_contents('php://input');
//         $jsonObject = (json_decode($value , true));
//         $nomor = (isset($jsonObject["nomor"]) ? $this->clean($jsonObject["nomor"])     : "");  //input merupakan nomor thorderjual
//         //data yang diperlukan
//         //nomorbarang~kodebarang~namabarang~satuan~price~qty~fee~disc~subtotal~notes
//         $query = "SELECT a.nomorbarang, a.kodebarang, b.nama namabarang, b.satuan, a.harga price, a.qty, a.fee, a.disc1 disc, a.subtotal, a.keterangandetail AS notes
//                   FROM tdorderjual a
//                   JOIN vwbarang b ON a.nomorbarang = b.nomor
//                   WHERE
//                     a.nomorheader = $nomor
//                     AND a.nomorpekerjaan = ''
//                     AND a.kodepekerjaan = '' ";
//         $result = $this->db->query($query);
//         if( $result && $result->num_rows() > 0){
//             foreach ($result->result_array() as $r){
//                 array_push($data['data'], array(
//                                                 'nomorbarang'	=> $r['nomorbarang'],
//                                                 'kodebarang'	=> $r['kodebarang'],
//                                                 'namabarang'	=> $r['namabarang'],
//                                                 'satuan'    	=> $r['satuan'],
//                                                 'price'     	=> $r['price'],
//                                                 'qty'       	=> $r['qty'],
//                                                 'fee'	        => $r['fee'],
//                                                 'disc'	        => $r['disc'],
//                                                 'subtotal'  	=> $r['subtotal'],
//                                                 'notes'     	=> $r['notes']
//                                                 )
//                 );
//             }
//         }else{
//             array_push($data['data'], array( 'query' => $this->error($query) ));
//         }

//         if ($data){
//             // Set the response and exit
//             $this->response($data['data']); // OK (200) being the HTTP response code
//         }
//     }

//     //added by Tonny
//     //untuk mendapatkan list pekerjaan berdasarkan nomor thorderjual
//     function getSalesOrderPekerjaanList_post(){
//         $data['data'] = array();
//         $value = file_get_contents('php://input');
//         $jsonObject = (json_decode($value , true));
//         $nomor = (isset($jsonObject["nomor"]) ? $this->clean($jsonObject["nomor"])     : "");  //input merupakan nomor thorderjual
//         //data yang diperlukan
//         //nomorbarang~kodebarang~namabarang~satuan~price~qty~fee~disc~subtotal~notes
//         $query = "SELECT a.nomorpekerjaan AS nomorbarang, a.kodepekerjaan AS kodebarang, b.nama namabarang, b.satuan, a.harga price, a.qty, a.fee, a.disc1 disc, a.subtotal, a.keterangandetail AS notes
//                   FROM tdorderjual a
//                   JOIN vwpekerjaan b ON a.nomorpekerjaan = b.nomor
//                   WHERE
//                     a.nomorheader = $nomor
//                     AND a.nomorbarang is null
//                     AND a.kodebarang is null ";
//         $result = $this->db->query($query);
//         if( $result && $result->num_rows() > 0){
//             foreach ($result->result_array() as $r){
//                 array_push($data['data'], array(
//                                                 'nomorbarang'	=> $r['nomorbarang'],
//                                                 'kodebarang'	=> $r['kodebarang'],
//                                                 'namabarang'	=> $r['namabarang'],
//                                                 'satuan'    	=> $r['satuan'],
//                                                 'price'     	=> $r['price'],
//                                                 'qty'       	=> $r['qty'],
//                                                 'fee'	        => $r['fee'],
//                                                 'disc'	        => $r['disc'],
//                                                 'subtotal'  	=> $r['subtotal'],
//                                                 'notes'     	=> $r['notes']
//                                                 )
//                 );
//             }
//         }else{
//             array_push($data['data'], array( 'query' => $this->error($query) ));
//         }

//         if ($data){
//             // Set the response and exit
//             $this->response($data['data']); // OK (200) being the HTTP response code
//         }
//     }

//     //added by Tonny
//     //untuk mendapatkan summary dari thorderjual
//     function getSalesOrderSummary_post(){
//         $data['data'] = array();
//         $value = file_get_contents('php://input');
//         $jsonObject = (json_decode($value , true));
//         $nomor = (isset($jsonObject["nomor"]) ? $this->clean($jsonObject["nomor"])     : "");  //input merupakan nomor thorderjual
//         //data yang diperlukan
//         //tanggal~customer~broker~valuta~subtotal~grandtotal
//         $query = "SELECT a.tanggal, b.nama namacustomer, c.nama namabroker, a.valuta, a.subtotal, a.disc, a.discnominal, a.ppn, a.ppnnominal, a.total
//                   FROM thorderjual a
//                       JOIN tcustomer b ON a.nomorcustomer = b.nomor
//                       JOIN thbroker c ON a.nomorbroker = c.nomor
//                   WHERE
//                     a.nomor = $nomor";
//         $result = $this->db->query($query);
//         if( $result && $result->num_rows() > 0){
//             foreach ($result->result_array() as $r){
//                 array_push($data['data'], array(
//                                                 'tanggal'   	=> $r['tanggal'],
//                                                 'namacustomer'	=> $r['namacustomer'],
//                                                 'namabroker'	=> $r['namabroker'],
//                                                 'valuta'    	=> $r['valuta'],
//                                                 'subtotal'     	=> $r['subtotal'],
//                                                 'disc'       	=> $r['disc'],
//                                                 'discnominal'	=> $r['discnominal'],
//                                                 'ppn'	        => $r['ppn'],
//                                                 'ppnnominal'  	=> $r['ppnnominal'],
//                                                 'total'     	=> $r['total']
//                                                 )
//                 );
//             }
//         }else{
//             array_push($data['data'], array( 'query' => $this->error($query) ));
//         }

//         if ($data){
//             // Set the response and exit
//             $this->response($data['data']); // OK (200) being the HTTP response code
//         }
//     }

//     // by Tonny
//     //Untuk insert data customer prospecting ke tabel tcustomerprospecting
//     function setApprove_post(){
//         $data['data'] = array();
//         $value = file_get_contents('php://input');
//         $jsonObject = (json_decode($value , true));
//         $nomor = (isset($jsonObject["nomor"]) ? $this->clean($jsonObject["nomor"])     : "");  //input merupakan nomor thorderjual
//         //$nomor = '253';
//         $query = "SELECT approve FROM thorderjual WHERE nomor = $nomor ";
//         $result = $this->db->query($query);
// //        echo $result->row()->approve;
//         $approve = 1;
//         $errormsg = '';
//         //cek jika sudah diapprove atau belum
//         if($result && $result->num_rows() > 0){
//             $approve = $result->row()->approve;
//             if($approve == 0){
//                 //jika belum diapprove, maka lanjutkan untuk memastikan hargajual barang > hpp
//                 $query = "SELECT a.hpp, a.hargajualidr
//                           FROM tbarang a
//                           JOIN tdorderjual b
//                             ON a.nomor = b.nomorbarang
//                           WHERE b.nomorheader = $nomor";
//                 $result = $this->db->query($query);
//                 $hpp = 0;
//                 if($result && $result->num_rows() > 0){
//                     $iserror = false;
//                     //pengecekan jika harga jual barang melebihi hpp
//                     foreach ($result->result_array() as $row){
//                         if($row['hpp'] >= $row['hargajualidr']){
//                             $iserror = true;
//                             $errormsg = 'harga jual value must be more than hpp value';
//                         }elseif($row['hpp'] <= 0){  //pengecekan jika hpp = 0
//                             $iserror = true;
//                             $errormsg = 'hpp value must be more than 0';
//                         }
//                     }
//                     if($iserror){
//                         array_push($data['data'], array( 'error' => $this->error($errormsg) ));
//                     }else{
//                         $query = "UPDATE thorderjual set approve = 1 WHERE nomor = $nomor ";
//                         $result = $this->db->query($query);
//                         if($result){
//                             array_push($data['data'], array( 'success' => 'true' ));
//                         }else{
//                             array_push($data['data'], array( 'error' => $this->error($query) ));
//                         }
//                         if ($data){
//                             // Set the response and exit
//                             $this->response($data['data']); // OK (200) being the HTTP response code
//                         }
//                     }
// 				}else{
// 				    $errormsg = 'data not found';
// 				    array_push($data['data'], array( 'error' => $this->error($errormsg) ));
// 				}
//             }else{
//                 $errormsg = 'data has been already approved';
//                 array_push($data['data'], array( 'error' => $this->error($errormsg) ));
//             }
//         }else{
//             $errormsg = 'data not found';
//             array_push($data['data'], array( 'error' => $this->error($errormsg) ));
//         }
//     }
}
