package org.maoxin.zkapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.hb.dialog.myDialog.MyImageMsgDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    public ArrayList<BluetoothDevice> deviceArrayList;
    public ArrayList<Boolean> deviceConnectedArray;
    public BluetoothDevice connectedDevice;
    // 用于检查重复设备
    public Map<String, BluetoothDevice> deviceMap;
    public DeviceListAdapter mDeviceListAdapter;
    public int record_enable = 0;
    public int lastClick = 0;
    BluetoothAdapter mBluetoothAdapter;
    ListView lvNewDevices;
    BluetoothGatt BG;
    Button discover_button;
    TextView valueView;
    TextView intervalView;
    Context context;
    TextView HR;
    Spinner spinner;
    int[] buffer_x = {70000, 70000, 70000, 70000, 70000, 70000};
    int[] buffer_y = {70000, 70000, 70000, 70000, 70000, 70000};
    int buffer_control = 0;
    int R_detect_flag = 0;
    int[] R_detect_array;
    int[] R_interval;
    int detect_length = 1600;
    int plot_control_mul = 2;
    double[] hf_coff = {-0.000241862601478186, -0.00618333017151190, -0.00292015458774930, -0.00352860546326712, -0.00381533991703169, -0.00390240672340895, -0.00377155150017023, -0.00344323716575758, -0.00279819458188298, -0.00601017086427698, -3.23155675236391e-05, 0.000212473328685507, 0.000214100793306813, 0.000216402527767417, 0.000218739282812823, 0.000221743351354569, 0.000224804101431031, 0.000228482819103074, 0.000232195938703678, 0.000236564217288778, 0.000240943767462462, 0.000245970851519319, 0.000250998652576486, 0.000256657745943320, 0.000262331953388789, 0.000268593985116165, 0.000274871920464321, 0.000281780760067274, 0.000288670368598727, 0.000296166754004045, 0.000303612804371826, 0.000311621665358330, 0.000319681157459461, 0.000328270774612938, 0.000336820632867949, 0.000345946861787297, 0.000355001005403041, 0.000364616756871584, 0.000374146402646035, 0.000384209723779878, 0.000394269223207374, 0.000404791195744381, 0.000415224216487852, 0.000426184458410963, 0.000437027297644453, 0.000448379060034008, 0.000459765558908533, 0.000471333568861225, 0.000482911607282858, 0.000494986600433337, 0.000506906877652680, 0.000519305329636683, 0.000531529737419900, 0.000544200440201160, 0.000556806462295274, 0.000569559982355034, 0.000582328414402971, 0.000595530233654461, 0.000608485010925849, 0.000621823577937644, 0.000634900021395484, 0.000648342695208030, 0.000661555065762399, 0.000675117134004698, 0.000688403791089957, 0.000702043279205866, 0.000715381843108796, 0.000729054067871398, 0.000742407365294652, 0.000756063029655742, 0.000769407859054698, 0.000783014866177094, 0.000796269554108974, 0.000809811693810158, 0.000823010023445550, 0.000836534158739822, 0.000849804306071187, 0.000862880376581668, 0.000875875368957665, 0.000889015959093708, 0.000901726531231015, 0.000914637864425536, 0.000927098852298660, 0.000939745196939242, 0.000951980788164564, 0.000963985193933147, 0.000975902690662249, 0.000987863404503204, 0.000999301962470884, 0.00101085238151819, 0.00102187214629162, 0.00103295219110459, 0.00104356231803754, 0.00105420008369322, 0.00106426526370020, 0.00107438763009171, 0.00108391992383149, 0.00109349326064004, 0.00110246206252105, 0.00111142096023749, 0.00111981850897158, 0.00112814533048731, 0.00113582101515375, 0.00114348179432277, 0.00115048024202169, 0.00115744143033935, 0.00116371368951634, 0.00116987338019362, 0.00117535540074039, 0.00118073662429687, 0.00118538614602424, 0.00118993231236909, 0.00119372411711218, 0.00119738867541159, 0.00120027788790729, 0.00120297959160257, 0.00120492128143958, 0.00120665269011750, 0.00120751280477309, 0.00120804735319330, 0.00120726370147976, 0.00120395379003982, 0.00121034841823745, 0.00120782772642264, 0.00120616593551287, 0.00120439032045188, 0.00120167808111322, 0.00119854971248166, 0.00119423321954481, 0.00118768599169546, 0.00118849551267828, 0.00117826370380175, 0.00116964260821439, 0.00116155966230625, 0.00115288288038599, 0.00114412595970646, 0.00113455354716389, 0.00112473578487770, 0.00111404540735775, 0.00110309140380077, 0.00109119065810992, 0.00107899827062984, 0.00106583001694339, 0.00105234621155236, 0.00103788070284128, 0.00102305361433860, 0.00100725969895938, 0.000991109110440077, 0.000973924836167329, 0.000956366591383154, 0.000937760189315824, 0.000918783485189065, 0.000898855606782382, 0.000878439043818011, 0.000857013224487071, 0.000835192955475456, 0.000812311468299397, 0.000789039439625504, 0.000764706617741790, 0.000739983603268677, 0.000714263714007165, 0.000688023798852553, 0.000660774272953085, 0.000633122154475912, 0.000604403133739483, 0.000575285902687734, 0.000545145092325801, 0.000514393415934166, 0.000482782210449701, 0.000450732663425887, 0.000417602551447614, 0.000384065820239558, 0.000349452014218593, 0.000314433890628060, 0.000278384375382015, 0.000241768593738243, 0.000204319162481330, 0.000166397455591948, 0.000127376851742238, 8.79564657292383e-05, 4.74833967047798e-05, 6.63810489044910e-06, -3.52166166427070e-05, -7.74698170154019e-05, -0.000120743059008500, -0.000164376766574787, -0.000209035711985310, -0.000254039877429829, -0.000300054793305119, -0.000346403847545959, -0.000393727177543936, -0.000441399397317129, -0.000490021884272656, -0.000538928487812777, -0.000588779271352387, -0.000638904442534124, -0.000690024755427625, -0.000741945657849665, -0.000794112307623342, -0.000846866599757318, -0.000900534968419690, -0.000954463724692410, -0.00100931322615235, -0.00106440502022170, -0.00112039754519593, -0.00117694987402342, -0.00123361779035843, -0.00129089663487595, -0.00134902199135877, -0.00140732470603168, -0.00146645968876734, -0.00152576382459960, -0.00158582457829640, -0.00164609203427129, -0.00170713328715977, -0.00176828590882055, -0.00183020496494691, -0.00189221414612019, -0.00195496969209245, -0.00201780745711805, -0.00208132619171779, -0.00214496193493867, -0.00220926084010269, -0.00227356477602885, -0.00233853465262199, -0.00240350034764503, -0.00246912013565605, -0.00253473466327651, -0.00260090778059725, -0.00266704519661662, -0.00273375847047081, -0.00280037708395708, -0.00286755470661116, -0.00293461192481079, -0.00300220006183793, -0.00306965378901851, -0.00313755181866847, -0.00320531172702865, -0.00327353676801340, -0.00334155737096134, -0.00341003083372801, -0.00347825541575772, -0.00354517789333208, -0.00361483468403496, -0.00368359621189993, -0.00375210496384257, -0.00382099039889886, -0.00388955637275691, -0.00395844647831516, -0.00402702914165579, -0.00409462022410647, -0.00416463933065297, -0.00423293159934225, -0.00430063601640685, -0.00436857079613318, -0.00443606175889654, -0.00450377465941109, -0.00457103682869956, -0.00463844273595584, -0.00470535128738135, -0.00477240481916911, -0.00483890461156584, -0.00490551302352105, -0.00497152794905846, -0.00503760853558132, -0.00510307856094369, -0.00516854654472621, -0.00523338380974212, -0.00529822824769233, -0.00536237646399113, -0.00542647995954812, -0.00548982318221877, -0.00555304050484515, -0.00561563454279720, -0.00567802247336105, -0.00573963134371295, -0.00580109759609399, -0.00586172263644098, -0.00592217105107264, -0.00598174460932463, -0.00604108962521099, -0.00609967222311659, -0.00615789541165676, -0.00621521866947604, -0.00627227926006493, -0.00632838428419300, -0.00638419215480138, -0.00643910926114895, -0.00649351184744534, -0.00654699867703690, -0.00660014208053248, -0.00665224343118316, -0.00670396735022964, -0.00675461520852717, -0.00680484437331614, -0.00685404269786637, -0.00690262927149849, -0.00695022497422569, -0.00699736039329645, -0.00704332168212629, -0.00708877572881731, -0.00713303413937129, -0.00717676383550871, -0.00721932638367332, -0.00726132796114796, -0.00730210977070104, -0.00734233295951351, -0.00738130425982293, -0.00741969694326344, -0.00745682087666762, -0.00749334162154748, -0.00752861130129365, -0.00756324036148363, -0.00759659350844617, -0.00762935601598877, -0.00766087578240639, -0.00769190308490440, -0.00772195157877627, -0.00775006745468789, -0.00777780516092437, -0.00780463308405751, -0.00783007468280841, -0.00785480295372079, -0.00787814093886709, -0.00790077572794816, -0.00792219943021980, -0.00794181229522325, -0.00796123612781566, -0.00797965000057413, -0.00799660917368424, -0.00801281024646620, -0.00802758736085755, -0.00804158305078187, -0.00805421344617654, -0.00806604037878759, -0.00807643227509344, -0.00808605731032896, -0.00809424211442448, -0.00810165810383242, 0.000427892656374111, 0.210148774362507, 0.0961841951232815, 0.118230551261127, 0.129042660734055, 0.132877361306260, 0.129042660734055, 0.118230551261127, 0.0961841951232815, 0.210148774362507, 0.000427892656374112, -0.00810165810383242, -0.00809424211442448, -0.00808605731032896, -0.00807643227509344, -0.00806604037878758, -0.00805421344617655, -0.00804158305078187, -0.00802758736085755, -0.00801281024646620, -0.00799660917368424, -0.00797965000057413, -0.00796123612781566, -0.00794181229522325, -0.00792219943021980, -0.00790077572794816, -0.00787814093886709, -0.00785480295372079, -0.00783007468280841, -0.00780463308405751, -0.00777780516092437, -0.00775006745468789, -0.00772195157877627, -0.00769190308490440, -0.00766087578240639, -0.00762935601598877, -0.00759659350844617, -0.00756324036148363, -0.00752861130129365, -0.00749334162154748, -0.00745682087666761, -0.00741969694326344, -0.00738130425982293, -0.00734233295951351, -0.00730210977070104, -0.00726132796114797, -0.00721932638367332, -0.00717676383550871, -0.00713303413937129, -0.00708877572881731, -0.00704332168212629, -0.00699736039329645, -0.00695022497422569, -0.00690262927149849, -0.00685404269786638, -0.00680484437331614, -0.00675461520852717, -0.00670396735022964, -0.00665224343118316, -0.00660014208053248, -0.00654699867703690, -0.00649351184744534, -0.00643910926114895, -0.00638419215480138, -0.00632838428419299, -0.00627227926006493, -0.00621521866947604, -0.00615789541165676, -0.00609967222311659, -0.00604108962521099, -0.00598174460932463, -0.00592217105107264, -0.00586172263644098, -0.00580109759609399, -0.00573963134371295, -0.00567802247336105, -0.00561563454279720, -0.00555304050484515, -0.00548982318221877, -0.00542647995954812, -0.00536237646399113, -0.00529822824769233, -0.00523338380974212, -0.00516854654472621, -0.00510307856094369, -0.00503760853558132, -0.00497152794905846, -0.00490551302352105, -0.00483890461156584, -0.00477240481916911, -0.00470535128738135, -0.00463844273595584, -0.00457103682869956, -0.00450377465941109, -0.00443606175889654, -0.00436857079613318, -0.00430063601640685, -0.00423293159934225, -0.00416463933065297, -0.00409462022410647, -0.00402702914165579, -0.00395844647831516, -0.00388955637275691, -0.00382099039889886, -0.00375210496384257, -0.00368359621189993, -0.00361483468403496, -0.00354517789333208, -0.00347825541575772, -0.00341003083372801, -0.00334155737096134, -0.00327353676801340, -0.00320531172702865, -0.00313755181866847, -0.00306965378901851, -0.00300220006183793, -0.00293461192481079, -0.00286755470661116, -0.00280037708395708, -0.00273375847047081, -0.00266704519661662, -0.00260090778059725, -0.00253473466327651, -0.00246912013565605, -0.00240350034764503, -0.00233853465262199, -0.00227356477602885, -0.00220926084010269, -0.00214496193493867, -0.00208132619171779, -0.00201780745711805, -0.00195496969209245, -0.00189221414612019, -0.00183020496494691, -0.00176828590882055, -0.00170713328715977, -0.00164609203427129, -0.00158582457829640, -0.00152576382459960, -0.00146645968876734, -0.00140732470603168, -0.00134902199135877, -0.00129089663487595, -0.00123361779035843, -0.00117694987402342, -0.00112039754519593, -0.00106440502022170, -0.00100931322615235, -0.000954463724692410, -0.000900534968419690, -0.000846866599757318, -0.000794112307623342, -0.000741945657849665, -0.000690024755427625, -0.000638904442534124, -0.000588779271352386, -0.000538928487812777, -0.000490021884272656, -0.000441399397317130, -0.000393727177543936, -0.000346403847545958, -0.000300054793305119, -0.000254039877429829, -0.000209035711985310, -0.000164376766574787, -0.000120743059008500, -7.74698170154018e-05, -3.52166166427070e-05, 6.63810489044910e-06, 4.74833967047798e-05, 8.79564657292383e-05, 0.000127376851742238, 0.000166397455591948, 0.000204319162481330, 0.000241768593738243, 0.000278384375382015, 0.000314433890628060, 0.000349452014218593, 0.000384065820239558, 0.000417602551447614, 0.000450732663425887, 0.000482782210449701, 0.000514393415934167, 0.000545145092325801, 0.000575285902687734, 0.000604403133739483, 0.000633122154475912, 0.000660774272953085, 0.000688023798852553, 0.000714263714007165, 0.000739983603268677, 0.000764706617741790, 0.000789039439625505, 0.000812311468299397, 0.000835192955475456, 0.000857013224487071, 0.000878439043818011, 0.000898855606782382, 0.000918783485189065, 0.000937760189315824, 0.000956366591383154, 0.000973924836167329, 0.000991109110440078, 0.00100725969895938, 0.00102305361433860, 0.00103788070284128, 0.00105234621155236, 0.00106583001694339, 0.00107899827062984, 0.00109119065810992, 0.00110309140380077, 0.00111404540735775, 0.00112473578487770, 0.00113455354716389, 0.00114412595970646, 0.00115288288038599, 0.00116155966230625, 0.00116964260821439, 0.00117826370380175, 0.00118849551267828, 0.00118768599169546, 0.00119423321954481, 0.00119854971248166, 0.00120167808111322, 0.00120439032045188, 0.00120616593551287, 0.00120782772642264, 0.00121034841823745, 0.00120395379003982, 0.00120726370147976, 0.00120804735319330, 0.00120751280477309, 0.00120665269011750, 0.00120492128143958, 0.00120297959160257, 0.00120027788790729, 0.00119738867541159, 0.00119372411711218, 0.00118993231236909, 0.00118538614602424, 0.00118073662429687, 0.00117535540074039, 0.00116987338019361, 0.00116371368951634, 0.00115744143033935, 0.00115048024202169, 0.00114348179432277, 0.00113582101515375, 0.00112814533048731, 0.00111981850897158, 0.00111142096023749, 0.00110246206252105, 0.00109349326064004, 0.00108391992383149, 0.00107438763009171, 0.00106426526370020, 0.00105420008369322, 0.00104356231803754, 0.00103295219110459, 0.00102187214629162, 0.00101085238151819, 0.000999301962470884, 0.000987863404503204, 0.000975902690662249, 0.000963985193933147, 0.000951980788164564, 0.000939745196939242, 0.000927098852298660, 0.000914637864425536, 0.000901726531231015, 0.000889015959093708, 0.000875875368957665, 0.000862880376581668, 0.000849804306071186, 0.000836534158739822, 0.000823010023445550, 0.000809811693810157, 0.000796269554108974, 0.000783014866177094, 0.000769407859054698, 0.000756063029655742, 0.000742407365294652, 0.000729054067871398, 0.000715381843108796, 0.000702043279205866, 0.000688403791089957, 0.000675117134004698, 0.000661555065762400, 0.000648342695208030, 0.000634900021395484, 0.000621823577937644, 0.000608485010925849, 0.000595530233654461, 0.000582328414402971, 0.000569559982355034, 0.000556806462295274, 0.000544200440201160, 0.000531529737419900, 0.000519305329636683, 0.000506906877652680, 0.000494986600433337, 0.000482911607282858, 0.000471333568861225, 0.000459765558908533, 0.000448379060034008, 0.000437027297644453, 0.000426184458410963, 0.000415224216487852, 0.000404791195744381, 0.000394269223207374, 0.000384209723779878, 0.000374146402646035, 0.000364616756871584, 0.000355001005403041, 0.000345946861787297, 0.000336820632867949, 0.000328270774612938, 0.000319681157459462, 0.000311621665358330, 0.000303612804371826, 0.000296166754004045, 0.000288670368598727, 0.000281780760067274, 0.000274871920464321, 0.000268593985116165, 0.000262331953388789, 0.000256657745943320, 0.000250998652576486, 0.000245970851519319, 0.000240943767462462, 0.000236564217288778, 0.000232195938703678, 0.000228482819103074, 0.000224804101431031, 0.000221743351354569, 0.000218739282812823, 0.000216402527767417, 0.000214100793306813, 0.000212473328685507, -3.23155675236391e-05, -0.00601017086427698, -0.00279819458188298, -0.00344323716575758, -0.00377155150017023, -0.00390240672340895, -0.00381533991703169, -0.00352860546326712, -0.00292015458774930, -0.00618333017151190, -0.000241862601478186};
    int[] ECG_buffer;
    int[] ECG_T;
    int buffer_x1[];
    int last_time_index = 0;
    Button HRV_timer;
    int HRV_timer_cancel = 0;
    TextView cd_timer;
    Button HRV_show;
    CreateUserDialog createUserDialog;
    int time_5_control = 0;
    boolean sig_ok = false;
    String set_once = null;
    Button savetxt;
    String filePath = "/sdcard/ECG/";
    String fileName = "data.txt";
    File file;
    RandomAccessFile raf = null;
    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_save_pop:

                    String name = createUserDialog.text_name.getText().toString().trim();
                    String age = createUserDialog.text_age.getText().toString().trim();
                    RadioButton rb = createUserDialog.findViewById(createUserDialog.text_sex.getCheckedRadioButtonId());
                    String sex = "未填写";
                    if (rb != null)
                        sex = rb.getText().toString();
                    if ((rb == null) || (name.equals("")) || (age.equals(""))) {
                        Toast toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                        toast.setText("信息填写不完整，请重填");
                        toast.setGravity(Gravity.CENTER, 0, -10);
                        toast.show();
                    } else {
                        String fileName1 = "user.txt";
                        Date curDate = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        String dateStr = sdf.format(curDate);
                        String strContent = "\r\n" + dateStr + "\r\n" + "  姓名：" + name + "  年龄：" + age + "  性别：" + sex;
                        writeTxtToFile(strContent, filePath, fileName1);
                    }
                    //intervalView.setText(name + "——" + mobile + "——" + sex);
                    try {
                        createUserDialog.dismiss();
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    };
    int HRV_temp_control = 0;
    int HRV_control = 0;
    int HRV_5m_control = 0;
    ImageButton user_info;
    boolean discover_enable = false;
    int index;
    boolean index_flag;
    private ArrayBlockingQueue<Float> ECGdatas;
    private ArrayBlockingQueue<Integer> ECGdatasSave;
    private Random random = new Random();
    private int[] HRV;
    private int[] HRV_temp;
    private int[] HRV5;
    private ArrayList<String> HRV_timestamp;
    private ArrayList<Integer> mean;
    private ArrayList<Integer> StandardDiviation;
    private ArrayList<Integer> rMSSD;
    private int ECG_amplitude = 1;
    private CountDownTimer cdTimer = new CountDownTimer(10000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            cd_timer.setVisibility(View.VISIBLE);
            cd_timer.setText((millisUntilFinished / 1000) + " s");
        }

        @Override
        public void onFinish() {
            cd_timer.setText("0 s");
            cd_timer.setVisibility(View.INVISIBLE);
            HRV_show.setVisibility(View.VISIBLE);
            //HRV_timer.setText("开始HRV监测");
            //HRV_timer_cancel=0;
            for (int i = 0; i < HRV_temp_control; i++) {
                HRV[i] = HRV_temp[i];

                HRV5[HRV_5m_control++] = HRV_temp[i];
                HRV_temp[i] = 0;
            }
            HRV_control = HRV_temp_control;
            HRV_temp_control = 0;
            int mean1 = mean(HRV, HRV_control);
            mean.add(mean1);

            Date curDate;
            curDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            String dateStr = sdf.format(curDate);
            HRV_timestamp.add(dateStr);

            if (time_5_control < 5) {
                time_5_control++;
            } else {
                time_5_control = 0;
                double StandardDiviation1 = 10 * StandardDiviation(HRV5, HRV_5m_control);
                StandardDiviation.add((int) StandardDiviation1);
                double rMSSD1 = 10 * rMSSD_com(HRV5, HRV_5m_control);
                rMSSD.add((int) rMSSD1);

                HRV_5m_control = 0;
            }
            cdTimer.start();
        }


    };

    private static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public int max(int[] Detect_array, int start, int end) {
        int temp1 = Detect_array[start];
        int temp2 = Detect_array[start];
        int data_length = end - start;
        for (int i = 1; i < data_length; i++) {
            if (Detect_array[start + i] > temp1) {
                temp2 = temp1;
                temp1 = Detect_array[start + i];
            } else if (Detect_array[start + i] < temp1 && Detect_array[start + i] > temp2) {
                temp2 = Detect_array[start + i];
            }
        }
        int temp[] = {temp1, temp2};
        return temp1;
    }

    public int[] max_pos(int[] Detect_array, int start, int end) {
        int temp1 = Detect_array[start];
        int pos1 = start;
        int data_length = end - start;
        for (int i = 1; i < data_length; i++) {
            if (Detect_array[start + i] > temp1) {
                temp1 = Detect_array[start + i];
                pos1 = start + i;
            }

        }
        int temp[] = {temp1, pos1};
        return temp;
    }

    public int mean_comp(int[] mean, int count) {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum += mean[i];
        }
        return sum;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HRV_timer_cancel = 0;
        HRV_temp_control = 0;

        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setContentView(R.layout.activity_main);
        this.context = this;

        valueView = findViewById(R.id.valueView);
        intervalView = findViewById(R.id.interval);
        lvNewDevices = findViewById(R.id.list);
        deviceArrayList = new ArrayList<>(128);
        deviceConnectedArray = new ArrayList<>(128);
        deviceMap = new ConcurrentHashMap<>();
        lvNewDevices.setOnItemClickListener(this);
        mDeviceListAdapter = new DeviceListAdapter(context, R.layout.device_adapter_view, deviceArrayList, deviceConnectedArray);
        lvNewDevices.setAdapter(mDeviceListAdapter);

        savetxt = findViewById(R.id.Record);
        discover_button = findViewById(R.id.scanButton);
        ECGdatas = new ArrayBlockingQueue<Float>(1024);
        ECGdatasSave = new ArrayBlockingQueue<Integer>(1024);

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override

            public void onItemSelected(AdapterView<?> parent, View view,

                                       int pos, long id) {

                String[] amplitude = getResources().getStringArray(R.array.amplitude);

                Toast.makeText(MainActivity.this, "您选择的增益是:" + amplitude[pos], Toast.LENGTH_SHORT).show();
                ECG_amplitude = pos + 1;

            }

            @Override

            public void onNothingSelected(AdapterView<?> parent) {

                // Another interface callback

            }

        });
        HR = findViewById(R.id.HR);
        R_detect_array = new int[detect_length];
        R_interval = new int[5];
        buffer_x1 = new int[1000];
        for (int i = 0; i < 1000; i++) {
            buffer_x1[i] = 70000;
        }
        ECG_buffer = new int[1009];
        for (int i = 0; i < 1009; i++) {
            ECG_buffer[i] = 0;
        }
        ECG_T = new int[1009];
        for (int i = 0; i < 1009; i++) {
            ECG_T[i] = 0;
        }
        HRV = new int[600];
        for (int i = 0; i < 600; i++) {
            HRV[i] = 0;
        }
        HRV_temp = new int[600];
        for (int i = 0; i < 600; i++) {
            HRV_temp[i] = 0;
        }
        HRV5 = new int[800];
        for (int i = 0; i < 800; i++) {
            HRV5[i] = 0;
        }
        mean = new ArrayList<>(200);
        HRV_timestamp = new ArrayList<>(200);

        StandardDiviation = new ArrayList<>(40);

        rMSSD = new ArrayList<>(40);

        cd_timer = findViewById(R.id.cd_timer);
        HRV_show = findViewById(R.id.HRV_report);
        HRV_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, hrv_analysis.class);
                //intent.putExtra("HRV",HRV);
                //intent.putExtra("HRV_control",HRV_control);
                intent.putExtra("HR", mean);
                intent.putExtra("HRV_timestamp", HRV_timestamp);
                intent.putExtra("SD", StandardDiviation);
                intent.putExtra("rMSSD", rMSSD);
                startActivityForResult(intent, 1);
            }
        });
        HRV_timer = findViewById(R.id.hrv_skip);
        HRV_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connectedDevice != null) {
                    if (HRV_timer_cancel == 0) {
                        if (sig_ok == true) {
                            cdTimer.start();
                            cd_timer.setVisibility(View.VISIBLE);
                            HRV_timer.setText("Cancel");
                            HRV_timer_cancel = 1;
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                            toast.setText("请在心电质量较好时进行HRV监测及记录");
                            toast.setGravity(Gravity.CENTER, 0, -10);
                            toast.show();
                        }

                    } else {
                        cdTimer.cancel();
                        for (int i = 0; i < 600; i++) {
                            HRV_temp[i] = 0;
                        }
                        HRV_temp_control = 0;
                        cd_timer.setVisibility(View.INVISIBLE);
                        HRV_timer_cancel = 0;
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date(System.currentTimeMillis());
                            String time = sdf.format(date);

                            final MyAlertInputDialog myAlertInputDialog = new MyAlertInputDialog(context).builder()
                                    .setTitle("是否要保存本次HRV数据？\r\n若要保存，请输入文件名：")
                                    .setEditText(time)
                                    .setCancelable(false);

                            myAlertInputDialog.setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String filePath = "/sdcard/ECG/HRV/";
                                    String fileName = myAlertInputDialog.getResult() + ".txt";

                                    makeFilePath(filePath, fileName);

                                    String strFilePath = filePath + fileName;
                                    // 每次写入时，都换行写
                                    String strContent = "HRV\r\n";
                                    File file;
                                    try {
                                        file = new File(strFilePath);
                                        if (!file.exists()) {
                                            Log.d("TestFile", "Create the file:" + strFilePath);
                                            file.getParentFile().mkdirs();
                                            file.createNewFile();
                                        }
                                        RandomAccessFile raf = null;
                                        raf = new RandomAccessFile(file, "rw");
                                        raf.seek(file.length());
                                        raf.write(strContent.getBytes());
                                        for (int i = 0; i < mean.size(); i++) {
                                            strContent = HRV_timestamp.get(i) + "  平均心率：  " + mean.get(i).toString();
                                            if (i % 5 == 4)
                                                strContent += "  SDNN：  " + (float) ((StandardDiviation.get((int) (i / 5))) / 10) + "  rMSSD：  " + (float) (rMSSD.get((int) (i / 5)) / 10);
                                            strContent += "\r\n";
                                            raf.write(strContent.getBytes());
                                        }
                                        raf.close();

                                    } catch (Exception e) {
                                        Log.e("TestFile", "Error on write File:" + e);
                                    }


                                    Toast toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                                    toast.setText(myAlertInputDialog.getResult());
                                    toast.setGravity(Gravity.CENTER, 0, -10);
                                    toast.show();
                                    myAlertInputDialog.dismiss();
                                }
                            }).setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    myAlertInputDialog.dismiss();
                                }
                            });
                            myAlertInputDialog.show();
                        }
                        HRV_timer.setText("开始HRV监测");

                    }
                }
            }
        });
        user_info = findViewById(R.id.user_info);
        user_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog(this);
            }
        });


        /*
        开启蓝牙，检查权限
         */
        boolean isGranted = true;
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBtIntent);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {


            if (this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE") != PERMISSION_GRANTED) {
                isGranted = false;
            } else {
                Log.e("Tip", "checkPermission: 已经授权1！");
            }
            if (this.checkSelfPermission("Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS") != PERMISSION_GRANTED) {
                isGranted = false;
            } else {

                Log.e("Tip", "checkPermission: 已经授权2！");
            }
            if (this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION") != PERMISSION_GRANTED) {
                isGranted = false;
            } else {
                Log.e("Tip", "checkPermission: 已经授权3！");
            }
            if (this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION") != PERMISSION_GRANTED) {
                isGranted = false;
            } else {
                Log.e("Tip", "checkPermission: 已经授权4！");
            }

            if (isGranted != true) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1001); //Any number
            }
        } else {
            Log.d("Tip", "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                float ECGdataT = 0;
                Electrocardiogram electrocardiogram = findViewById(R.id.electrocardiogram1);
                int data_numbers = 0;
                int x_axis = 0;
                // electrocardiogram.electrocardDatas.clear();
                electrocardiogram.electrocarPath.reset();
                int redraw = 0;
                int R_interval_index = 0;

                while (true) {
                    try {

                        ECGdataT = ECGdatas.take();

                        if (plot_control_mul >= 1) {
                            float ECGdata = (float) (ECGdataT * 3.6 * ECG_amplitude / 16);
                            data_numbers++;
                            x_axis = data_numbers % 816;
                            redraw++;
                            electrocardiogram.electrocardDatas.set(x_axis, ECGdata);
                            electrocardiogram.show_index = x_axis - 10;
                            if (redraw >= 8) {
                                electrocardiogram.invalidate();
                                redraw = 0;
                            }
                            plot_control_mul = 0;
                        } else {
                            plot_control_mul++;
                        }
                        if (R_detect_flag < detect_length) {
                            R_detect_array[R_detect_flag] = (int) ECGdataT;
                            R_detect_flag++;
                        } else {
                            int[] startR = new int[detect_length];
                            int[] endR = new int[detect_length];
                            int[] maxR = new int[detect_length];
                            int[] posR = new int[detect_length];
                            int[] interval = new int[detect_length];

                            int threshold = max(R_detect_array, 0, detect_length - 1);
                            threshold = threshold - Math.abs((int) (threshold * 0.25));
                            int R_flag = 0;
                            int k = 0;

                            for (int i = 0; i < detect_length; i++) {
                                if (R_detect_array[i] > threshold && R_flag == 0) {
                                    startR[k] = i;
                                    R_flag = 1;
                                } else if (R_detect_array[i] < threshold && R_flag == 1) {
                                    endR[k] = i;
                                    R_flag = 0;
                                    int[] temp = max_pos(R_detect_array, startR[k], endR[k]);
                                    maxR[k] = temp[0];
                                    posR[k] = temp[1];

                                    k++;
                                }
                            }
                            int q = 0;
                            int posPast = posR[0];
                            if (last_time_index != 0 && posR[0] + (detect_length - last_time_index) > 200 && (posR[0] + (detect_length - last_time_index) < 600)) {
                                interval[q] = posR[0] + (detect_length - last_time_index);
                                q++;
                            }
                            if (k > 0) {
                                last_time_index = posR[k - 1];
                            }
                            if (k <= 8 && k >= 2) {
                                int R_number = k;
                                int i = 0;
                                while (i < k - 1) {
                                    if (posR[i + 1] - posPast < 200 || posR[i + 1] - posPast > 600) {
                                        posR[i + 1] = -1;
                                        i++;
                                        R_number = R_number - 1;
                                    } else {
                                        interval[q] = posR[i + 1] - posPast;
                                        posPast = posR[i + 1];
                                        q++;
                                    }
                                    i++;
                                }
                                if (R_number < 7 && R_number >= 2) {
                                    for (int p = 0; p < q; p++) {
                                        R_interval[R_interval_index] = interval[p];
                                        int hr = (30000 / R_interval[R_interval_index]);
                                        if (R_interval[4] != 0) {
                                            HR.setText(hr + " bpm");
                                            sig_ok = true;
                                            if (HRV_timer_cancel == 1) {
                                                HRV_temp[HRV_temp_control] = interval[p];
                                                HRV_temp_control++;
                                            }
                                        } else {
                                            //HR.setText("no signal");
                                            sig_ok = false;
                                        }
                                        R_interval_index = (R_interval_index + 1) % 5;
                                    }
                                    //intervalView.setText(R_interval[0]+" "+R_interval[1]+" "+R_interval[2]+" "+R_interval[3]+" "+R_interval[4]+"  "+q);
                                } else {
                                    for (int p = 0; p < 5; p++) {
                                        //R_interval[p] = 0;
                                        //HR.setText("no signal");
                                        sig_ok = false;
                                    }
                                    //R_interval_index = 0;
                                    //intervalView.setText(R_interval[0]+" "+R_interval[1]+" "+R_interval[2]+" "+R_interval[3]+" "+R_interval[4]+"  "+q);
                                }
                            }
                            if (k > 8 || k < 2) {
                                for (int p = 0; p < 5; p++) {
                                    //R_interval[p] = 0;
                                    //HR.setText("no signal");
                                    sig_ok = false;
                                }
                                //R_interval_index = 0;
                                //intervalView.setText(R_interval[0]+" "+R_interval[1]+" "+R_interval[2]+" "+R_interval[3]+" "+R_interval[4]+"  "+q);
                            }


                            R_detect_flag = 0;
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //electrocardiogram.startDraw();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int ECGdataSave = 0;
                int count = -1;
                Date curDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                String dateStr;
                String strContent = null;
                while (true) {
                    try {
                        ECGdataSave = ECGdatasSave.take();
                        strContent = strContent + "  " + ECGdataSave;
                        count++;
                        if (count == 50 || count == 0) {
                            strContent = strContent + "\r\n";
                            if (record_enable == 1) {
                                try {
                                    if (file.exists() && raf != null) {
                                        raf.write(strContent.getBytes());
                                    }

                                } catch (Exception e) {
                                    Log.e("TestFile", "Error on write File of ECG:" + e);
                                }

                            }
                            count = 0;
                            curDate = new Date();
                            dateStr = sdf.format(curDate);
                            strContent = dateStr;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }
//生成文件

    public void showEditDialog(View.OnClickListener view) {
        createUserDialog = new CreateUserDialog(this, 21, onClickListener);
        createUserDialog.show();
    }

//生成文件夹

    public void btnDiscover(View view) {
        if (discover_enable == false) {
            discover_enable = true;
            discover_button.setText("STOPSCAN");
            deviceArrayList.clear();
            deviceConnectedArray.clear();
            if (connectedDevice != null) {
                deviceArrayList.add(connectedDevice);
                deviceConnectedArray.add(true);
            }
            mDeviceListAdapter.notifyDataSetChanged();
            Log.d("Tip", "btnDiscover: Looking for unpaired devices.");

            Log.d("Tip", "enableDisableBT:disabling BT");
            ScanCallback scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    BluetoothDevice bluetoothDevice = result.getDevice();
                    if (deviceArrayList.contains(bluetoothDevice)) {
                        return;
                    }
                    String name = bluetoothDevice.getName();
                    if (name != null && name.contains("ECG")) {
                        deviceMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
                        deviceArrayList.add(bluetoothDevice);
                        deviceConnectedArray.add(false);
                        mDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            };
            Log.d("Tip", "Start scan");
            // 设置搜索条件
            List<ScanFilter> scanFilters = new LinkedList<ScanFilter>() {{
//                add(new ScanFilter.Builder().setDeviceName("ECG").build());
            }};
            mBluetoothAdapter.getBluetoothLeScanner()
                    .startScan(scanFilters,
                            new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
                            scanCallback);
            // 60s 后停止扫描

            new Handler().postDelayed(() -> {
                Log.d("Tip", "Stop scan");
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(scanCallback);
                discover_enable = false;
                discover_button.setText("SCANBT");
            }, 60_000);
        } else {
            discover_enable = false;
            mBluetoothAdapter.cancelDiscovery();
            discover_button.setText("SCANBT");

        }
    }

    // 将字符串写入到文本文件中
    private void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            raf = new RandomAccessFile(file, "rw");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    private void writeECGToFlie(int f) {
        Date curDate = new Date();
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(curDate);
        String strContent = dateStr + "  " + f + "\r\n";
        //String strContent = f + "\r\n";
        try {
            if (file.exists() && raf != null) {
                raf.write(strContent.getBytes());
            }
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File of ECG:" + e);
        }
    }

    private File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public void btnRecord(View view) throws FileNotFoundException {
        if (record_enable == 0) {
            record_enable = 1;
            savetxt.setText("StopRecord");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String time = sdf.format(date);
            fileName = time + ".txt";
            writeTxtToFile("\r\n", filePath, fileName);

//            Scanner sc = new Scanner(new FileReader("/sdcard/ECG/user.txt"));
//            String line;
//            while ((sc.hasNextLine() && (line = sc.nextLine()) != null)) {
//                if (!sc.hasNextLine())
//                    writeTxtToFile(line + "\r\n", filePath, fileName);
//            }
        } else {
            record_enable = 0;
            savetxt.setText("Record");
            file = null;
            try {
                raf.close();
            } catch (Exception e) {
            }
        }
    }
    boolean disconnect_initiative=false;
    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
        Log.d("Tip", "onItemClick: You Clicked on a device" + i + ".");
        String deviceName = deviceArrayList.get(i).getName();
        String deviceAddress = deviceArrayList.get(i).getAddress();

        Log.d("Tip", "onItemClick: deviceName = " + deviceName);
        Log.d("Tip", "onItemClick: deviceAddress = " + deviceAddress);
        lastClick = i;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d("Tip", "Trying to pair with " + deviceName);
            if (set_once == null) {
                MyImageMsgDialog myImageMsgDialog = new MyImageMsgDialog(this).builder()
                        .setImageLogo(getResources().getDrawable(R.mipmap.buletooth))
                        .setMsg("连接中...");
                myImageMsgDialog.setCanceledOnTouchOutside(false);
                myImageMsgDialog.show();
                //停止搜索
                discover_enable = false;
                mBluetoothAdapter.cancelDiscovery();
                discover_button.setText("SCANBT");
                disconnect_initiative=false;

                BG = deviceArrayList.get(i).connectGatt(this, false, new BluetoothGattCallback() {
                    double[] ECG_buffer = new double[689];
                    int ECG_buffer_control = 0;

                    @SuppressLint("SetTextI18n")
                    @Override

                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        Log.d("Tip", gatt.getDevice().getName() + ":" + status + " -> " + newState);
                        myImageMsgDialog.dismiss();
                        index = deviceArrayList.indexOf(deviceMap.get(gatt.getDevice().getAddress()));

                        if (newState == BluetoothProfile.STATE_CONNECTED) {

                            Log.i("Tip", "Attempting to start service discovery:" +
                                    gatt.discoverServices());

                            connectedDevice = deviceArrayList.get(index);
                            set_once = deviceArrayList.get(index).getAddress();
                            valueView.setText("Connected successfully");
                            //deviceConnectedArray.remove(index);
                            //deviceConnectedArray.add(index,true);
                            index_flag = true;
                            //runOnUiThread(() -> mDeviceListAdapter.notifyDataSetChanged());
                            new addViewsToList().execute();

                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                            Log.i("Tip", "Disconnected from GATT server.");
                            BG.close();
                            set_once = null;
                            connectedDevice = null;
                            //deviceConnectedArray.set(index, false);
                            //deviceConnectedArray.remove(index);
                            //deviceConnectedArray.add(index,false);
                            //runOnUiThread(() -> mDeviceListAdapter.notifyDataSetChanged());
                            index_flag = false;
                            new addViewsToList().execute();
                            valueView.setText("Disconnected");

                            cd_timer.setText("0 s");
                            cdTimer.cancel();
                            for (int i = 0; i < 600; i++) {
                                HRV_temp[i] = 0;
                            }
                            HRV_temp_control = 0;

                            cd_timer.setVisibility(View.INVISIBLE);
                            HRV_timer_cancel = 0;

                            if(disconnect_initiative==false){
                                Log.i("Tip", "device disconnect: false");
                                runOnUiThread(() -> onItemClick(parent, view, i, id));
                            }
                            Log.i("Tip", "device disconnect: true");

                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                                Log.d("Tip", bluetoothGattService.getUuid().toString());
                                for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                                    Log.d("Tip", "   " + bluetoothGattCharacteristic.getDescriptors()
                                            + ":" + bluetoothGattCharacteristic.getUuid() + ":" + bluetoothGattCharacteristic.getWriteType());
                                    Log.d("Tip", "test11111111");
                                    if (bluetoothGattCharacteristic.getUuid().equals(UUID.fromString("00001524-1212-efde-1523-785feabcd123"))) {
                                        Log.d("Tip", "find bluetoothGattDescriptor");
                                        for (BluetoothGattDescriptor bluetoothGattDescriptor : bluetoothGattCharacteristic.getDescriptors()) {
                                            Log.d("Tip", "      " + bluetoothGattDescriptor.getUuid().toString());
                                        }
                                        Log.d("Tip", "Enable notify:" + gatt.setCharacteristicNotification(bluetoothGattCharacteristic, true));


                                        BluetoothGattDescriptor descriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        gatt.writeDescriptor(descriptor);

                                    }
                                }
                            }
                        } else {
                            Log.w("Tip", "onServicesDiscovered received: " + status);
                        }

                    }

                    @Override
                    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                        super.onCharacteristicChanged(gatt, characteristic);
                        int bytes_number = characteristic.getValue().length;
                        byte[] buffer = new byte[bytes_number];
                        buffer = characteristic.getValue();

                        int k = 0;
                        //Log.d("Tip", "Data decoding1...");
                        while (k + 20 <= bytes_number) {
                            int data = 0;
                            for (int i = 0; i < 10; i = i + 1) {
                                data = (buffer[k + 2 * i] & 0xFF) * 256 + (buffer[k + 1 + 2 * i] & 0xFF);
                                if (data > 5000)
                                    data = 0;

                                try {
                                    ECGdatasSave.put(data);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                int ECG_buffer_index = ECG_buffer_control % 689;
                                ECG_buffer[ECG_buffer_index] = data;

                                double filtered = 0;
                                for (int j = 0; j < 689; j++) {
                                    filtered += hf_coff[j] * ECG_buffer[(ECG_buffer_index + 1 + j) % 689];
                                }

                                try {
                                    ECGdatas.put((float) filtered);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                ECG_buffer_control++;
                            }
                            k += 20;
                            //Log.d("Tip", "Data decoding...");

                        }

                    }
                });

            } else if (set_once == deviceArrayList.get(i).getAddress()) {
                disconnect_initiative = true;
                BG.disconnect();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                toast.setText("请断开现有连接后开始新连接");
                toast.setGravity(Gravity.CENTER, 0, -10);
                toast.show();
            }
        }

    }

    public int mean(int[] x, int m) {
        int hr_sum = 0;
        for (int i = 0; i < m; i++) {
            hr_sum += x[i];
        }
        int hr = 0;
        if (hr_sum != 0)
            hr = 60 * 500 * m / hr_sum;
        return hr;
    }

    public double StandardDiviation(int[] x, int m) {

        double sum = 0;
        for (int i = 0; i < m; i++) {//求和
            sum += (float) x[i] * 1000 / 512;
        }
        double dAve = sum / m;//求平均值

        double dVar = 0;
        for (int i = 0; i < m; i++) {//求方差
            dVar += ((float) x[i] * 1000 / 512 - dAve) * ((float) x[i] * 1000 / 512 - dAve);
        }
        return Math.sqrt(dVar / m);
    }

    public double rMSSD_com(int[] x, int m) {

        double sum = 0;
        if (m >= 2) {
            for (int i = 0; i < m - 1; i++) {
                sum += (float) (x[i + 1] - x[i]) * (x[i + 1] - x[i]);
            }

            sum = (float) sum * 15625 / 4096;
            return Math.sqrt(sum / (m - 1));
        }
        return 0;
    }

    private class addViewsToList extends AsyncTask<Void, Void, Boolean> {
        protected void onPostExecute(Boolean result) {

            deviceConnectedArray.set(index, index_flag);
            mDeviceListAdapter.notifyDataSetChanged();
            if (index_flag == false)
                HRV_timer.setText("开始HRV监测");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return true;
        }
    }
}