package src.pas.pokemon.senses;


import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

//import edu.bu.labs.rttt.agents.Agent;

// SYSTEM IMPORTS


// JAVA PROJECT IMPORTS
import edu.bu.pas.pokemon.agents.senses.SensorArray;
import edu.bu.pas.pokemon.core.Battle.BattleView;
import edu.bu.pas.pokemon.core.DamageEquation;
import edu.bu.pas.pokemon.core.Move;
import edu.bu.pas.pokemon.core.Move.Category;
import edu.bu.pas.pokemon.core.Move.MoveView;
import edu.bu.pas.pokemon.core.Pokemon;
import edu.bu.pas.pokemon.core.Pokemon.PokemonView;
import edu.bu.pas.pokemon.core.Team.TeamView;
import edu.bu.pas.pokemon.core.enums.Flag;
import edu.bu.pas.pokemon.core.enums.NonVolatileStatus;
import edu.bu.pas.pokemon.core.enums.Stat;
import edu.bu.pas.pokemon.core.enums.Type;
import edu.bu.pas.pokemon.linalg.Matrix;
import edu.bu.pas.pokemon.core.DamageEquation;
import edu.bu.pas.pokemon.core.Pokemon;



public class CustomSensorArray
    extends SensorArray
{

    // TODO: make fields if you want!
    //getmyteamidx -> decide which team is mine then pass into that team 
    private final int myIdx;
    

    public CustomSensorArray(int myTeamIdx)
    {
        // TODO: intialize those fields if you make any!
        this.myIdx = myTeamIdx;
    
    }


    public Matrix getSensorValues(final BattleView state, final MoveView action)
    {
        // TODO: Convert a BattleView and a MoveView into a row-vector containing measurements for every sense
        // you want your neural network to have. This method should be called if your model is a q-based model

        Matrix matrix_input = Matrix.zeros(1,8);

        int index = 0;

        TeamView team1 = state.getTeam1View();
        TeamView team2 = state.getTeam2View();

        TeamView my_team;
        TeamView opp_team;
        if(team1.getBattleIdx() == myIdx){
            my_team = team1;
            opp_team = team2;
           
        }else{
            my_team = team2;
            opp_team = team1;
        }

        PokemonView my_pokemon_view = my_team.getActivePokemonView();
        PokemonView opp_pokemon_view = opp_team.getActivePokemonView();

        //double hp_r = (double)my_pokemon_view.getCurrentStat(Stat.HP)/my_pokemon_view.getInitialStat(Stat.HP); 
        double hp_r = (double)my_pokemon_view.getCurrentStat(Stat.HP)/250.0; 
        matrix_input.set(0, index++, hp_r);
        //double hp_oppr = (double)opp_pokemon_view.getCurrentStat(Stat.HP)/opp_pokemon_view.getInitialStat(Stat.HP); 
        double hp_oppr = (double)opp_pokemon_view.getCurrentStat(Stat.HP)/250.0; 
        matrix_input.set(0, index++, hp_oppr);

        //calculate the ratio of the bench pokemons' current hp /5*250
        double tthp1 = 0.0;
        int myBenchPo= my_team.size()-1;
        for(int i = 0; i< myBenchPo; i++){ 
            PokemonView p = my_team.getPokemonView(i);
            if(p != null && p != my_team.getActivePokemonView()){
                tthp1 += (double)p.getCurrentStat(Stat.HP);  
            }
        }
        double tthp1_r = tthp1 / (myBenchPo*250);
        matrix_input.set(0,index++,tthp1_r);

        double tthp2 = 0.0;
        int oppBenchPo= opp_team.size()-1;
        for(int i = 0; i<oppBenchPo; i++){ 
            PokemonView op = opp_team.getPokemonView(i);
            if(op != null && op != opp_team.getActivePokemonView()){
                tthp2 += (double)op.getCurrentStat(Stat.HP);
            }
        }
        double tthp2_r = tthp2 / (oppBenchPo*250);
        matrix_input.set(0,index++,tthp2_r);

        matrix_input.set(0, index++, (double)action.getPriority()); 


        double accuracy = 1.0;
        if(action.getAccuracy()!=null){
            accuracy = action.getAccuracy()/100.0;
        }
        matrix_input.set (0, index++, accuracy);

        double damagewa =0.0;
        if(action != null && action.getPower()!=null){
            try{
                Move mo = new Move(action);
                Pokemon at = Pokemon.fromView(my_pokemon_view);
                Pokemon de = Pokemon.fromView(opp_pokemon_view);

                double dmg = DamageEquation.calculateDamage(mo, at, de, 1, 0.85);
                //damagewa = dmg * accuracy;
                damagewa = dmg /999.0;
                //damagewa = damagewa /999.0;
                //damagewa = damagewa /opp_pokemon_view.getBaseStat(Stat.HP);
                // devide by oppnent current hp

            }catch(Exception e){
                damagewa = 0.0;
            }
        }
        matrix_input.set (0, index++, damagewa);

        return matrix_input;


        //(ATK, DEF, SPD, SPATK, SPDEF, ACC, EVASIVE)->stage multiplier [-6,+6]  
        // Stat[] battle_Stats = {Stat.ATK, Stat.DEF, Stat.SPD, Stat.SPATK, Stat.SPDEF, Stat.ACC, Stat.EVASIVE};

        // for (Stat s : battle_Stats) {
        //     matrix_input.set(0, index++,  my_pokemon_view.getStatMultiplier(s) / 6.0);
        //     matrix_input.set(0, index++,  opp_pokemon_view.getStatMultiplier(s) / 6.0);
        // }

        // for(int i = 0; i<6; i++){
        //     double tthp1 = 0.0;
        //     PokemonView p = my_team.getPokemonView(i);
        //     if(p != null){
        //         tthp1 += (double)p.getCurrentStat(Stat.HP);
        //         double tthp1_r = tthp1 / (6*250);
        //         matrix_input.set(0,index++,tthp1_r);
                
        //     }
        // }
        // for(int i = 0; i<6; i++){
        //     double tthp2 = 0.0;
        //     PokemonView op = opp_team.getPokemonView(i);
        //     if(op != null){
        //         tthp2 += (double)op.getCurrentStat(Stat.HP);
        //         double tthp2_r = tthp2 / (6*250);
        //         matrix_input.set(0,index++,tthp2_r);
                
        //     }
        // }

        


        // int countm = 0;
        // for(int i=0; i<6; i++) {
        //     if(my_team.getPokemonView(i) != null && !my_team.getPokemonView(i).hasFainted()) 
        //         countm++;
        // }
        // matrix_input.set(0, index++, countm);

        // int counto = 0;
        // for(int i=0; i<6; i++) {
        //     if(opp_team.getPokemonView(i) != null && !opp_team.getPokemonView(i).hasFainted()) 
        //         counto++;
        // }
        // matrix_input.set(0, index++, counto);

        //NonVolatileStatus
        // NonVolatnileStatus my_status = my_pokemon_view.getNonVolatileStatus();
        // for (NonVolatileStatus s : NonVolatileStatus.values()) {
        //     if(my_status ==s){
        //         matrix_input.set(0, index++, 1.0);
        //     }else{
        //         matrix_input.set(0, index++, 0.0);
        //     }
        // }
        // NonVolatileStatus opp_status = opp_pokemon_view.getNonVolatileStatus();
        // for (NonVolatileStatus s : NonVolatileStatus.values()) {
        //     if(opp_status ==s){
        //         matrix_input.set(0, index++, 1.0);
        //     }else{
        //         matrix_input.set(0, index++, 0.0);
        //     }
        // }

        //flag: FOCUS_ENERGY, CONFUSED, TRAPPED, SEEDED, FLINCHED 
        // matrix_input.set(0, index++, my_pokemon_view.getFlag(Flag.FOCUS_ENERGY) ? 1.0 : 0.0);
        // matrix_input.set(0, index++, my_pokemon_view.getFlag(Flag.CONFUSED) ? 1.0 : 0.0);
        // matrix_input.set(0, index++, my_pokemon_view.getFlag(Flag.TRAPPED) ? 1.0 : 0.0);
        // matrix_input.set(0, index++, my_pokemon_view.getFlag(Flag.SEEDED) ? 1.0 : 0.0);
        // matrix_input.set(0, index++, my_pokemon_view.getFlag(Flag.FLINCHED) ? 1.0 : 0.0);


        // matrix_input.set(0, index++, opp_pokemon_view.getFlag(Flag.FOCUS_ENERGY) ? 1.0 : 0.0);
        // matrix_input.set(0, index++, opp_pokemon_view.getFlag(Flag.CONFUSED) ? 1.0 : 0.0);
        // matrix_input.set(0, index++, opp_pokemon_view.getFlag(Flag.TRAPPED) ? 1.0 : 0.0);
        // matrix_input.set(0, index++, opp_pokemon_view.getFlag(Flag.SEEDED) ? 1.0 : 0.0);
        // matrix_input.set(0, index++, opp_pokemon_view.getFlag(Flag.FLINCHED) ? 1.0 : 0.0);


        // //type
        //Type[] my_types = {my_pokemon_view.getCurrentType1(), my_pokemon_view.getCurrentType2()};
        // for (Type t : Type.values()) {
        //     double has_type = (t == my_types[0] || t == my_types[1]) ? 1.0 : 0.0;
        //     matrix_input.set(0, index++, has_type);
        // }
        
        // //type
        // Type[] opp_types = {opp_pokemon_view.getCurrentType1(), opp_pokemon_view.getCurrentType2()};
        // for (Type t : Type.values()) {
        //     double has_type = (t == opp_types[0] || t == opp_types[1]) ? 1.0 : 0.0;
        //     matrix_input.set(0, index++, has_type);
        // }


        //Action
        // Category category = action.getCategory();
        // for(Category s : Category.values()){
        //     if(category == s){
        //         matrix_input.set(0, index++, 1.0);
        //     }else{
        //         matrix_input.set(0, index++, 0.0);
        //     } 
        // }

        
        // for (Type t : Type.values()) {
        //     if(move_type ==t){
        //         matrix_input.set(0, index++, 1.0);
        //     }else{
        //         matrix_input.set(0, index++, 0.0);
        //     }
            
        // }
        // isSuperEffective(Type attackingType, Type defendingType)



        // boolean eff1 = Type.isSuperEffective(move_type, opp_type1);

        // boolean eff2 = false;
        // if(opp_type2 != null){
        //     eff2 = Type.isSuperEffective(move_type, opp_type2);
        // }
        // double sp = 0.0;
        // if(eff1 || eff2){
        //     sp = 1.0;
        // }

        // matrix_input.set(0, index++, sp);
       
        // if(action.getAccuracy()!=null){
        //      matrix_input.set(0, index++, action.getAccuracy()/100.0); 
        // }
        // matrix_input.set(0, index++, 1.0); 
        
        
        // if(action.getPower()!=null){
        //     matrix_input.set(0, index++, action.getPower()/250); //explo
        // }
        // matrix_input.set(0, index++, 0.0);

        

        //Type my_type1 = my_pokemon_view.getCurrentType1();
        //Type my_type2 =  my_pokemon_view.getCurrentType2();

        //double type1 = Type.getEffectivenessModifier(action.getType(), opp_type1);
        //double type2 = Type.getEffectivenessModifier(action.getType(), opp_type2);




        // Type move_type = action.getType();

        // Type opp_type1 = opp_pokemon_view.getCurrentType1();
        // Type opp_type2 =  opp_pokemon_view.getCurrentType2();

        // boolean s = (move_type == my_pokemon_view.getCurrentType1()||
        //                 move_type == my_pokemon_view.getCurrentType2());
        // double stab;
        // if(s==true){
        //     stab = 1.5;
        // }else{
        //     stab = 1.0;
        // }

        // double damage;

        // double A = my_pokemon_view.getCurrentStat(Stat.ATK);
        // double D = opp_pokemon_view.getCurrentStat(Stat.DEF);

        // double power;
        // if(action.getPower()!=null){
        //     power = action.getPower();
        // }else{
        //     power = 0;
        // }

        // double part1 = ((2*my_pokemon_view.getLevel()/5+2)*power*(A/D))/50+2;

        // double t1 = Type.getEffectivenessModifier(move_type, opp_type1);
        // double t2;
        // if(opp_type2 != null){
        //     t2 = Type.getEffectivenessModifier(move_type, opp_type2);
        // }else{
        //     t2 = 1.0;
        // }
        // double type1_2 = t1 * t2;

        // damage = part1 * stab * type1_2 * Math.random();

        // double accuracy = 0.0;
        // if(action.getAccuracy()!=null){
        //     accuracy = action.getAccuracy()/100.0;
        // }


        // Move m = new Move(action);
        // Pokemon a = Pokemon.fromView(my_pokemon_view);
        // Pokemon d = Pokemon.fromView(opp_pokemon_view);
        // int damage;


        // damage = DamageEquation.calculateDamage(m, a, d, 1, 0.85);
        // damage *= accuracy;

        // double damage_r = damage * accuracy;

        // double damage_n  = damage_r/999.0;

        // matrix_input.set (0, index++, damage_n);
        // matrix_input.set (0, index++, damage);

       
    }



}
