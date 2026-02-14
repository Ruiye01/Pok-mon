package src.pas.pokemon.rewards;


// SYSTEM IMPORTS


// JAVA PROJECT IMPORTS
import edu.bu.pas.pokemon.agents.rewards.RewardFunction;
import edu.bu.pas.pokemon.agents.rewards.RewardFunction.RewardType;
import edu.bu.pas.pokemon.core.Battle.BattleView;
import edu.bu.pas.pokemon.core.Move.MoveView;
import edu.bu.pas.pokemon.core.Pokemon.PokemonView;
import edu.bu.pas.pokemon.core.Team.TeamView;
import edu.bu.pas.pokemon.core.enums.Stat;
import edu.bu.pas.pokemon.core.enums.Type;


public class CustomRewardFunction
    extends RewardFunction
{

    public CustomRewardFunction()
    {
        super(RewardType.STATE_ACTION_STATE); // currently configured to produce rewards as a function of the state
    
    }

    public double getLowerBound()
    {
        // TODO: change this. Reward values must be finite!

        return -100.0;
    }

    public double getUpperBound()
    {
        // TODO: change this. Reward values must be finite!
        return 100.0;
    }

    public double getStateReward(final BattleView state)
    {
        return 0d;
    }

    public double getStateActionReward(final BattleView state,
                                       final MoveView action)
    {

        return 0f;   

        }

    public double getStateActionStateReward(final BattleView state,
                                            final MoveView action,
                                            final BattleView nextState)
    {
        TeamView team1 = state.getTeam1View();
        TeamView team2 = state.getTeam2View();
        int myTeamIdx = getTeamIdx();

        TeamView my_team;
        TeamView opp_team;
        if(team1.getBattleIdx() == myTeamIdx){
            my_team = team1;
            opp_team = team2;
           
        }else{
            my_team = team2;
            opp_team = team1;
        }

        TeamView next_team1 = nextState.getTeam1View();
        TeamView next_team2 = nextState.getTeam2View();
        

        TeamView next_my_team;
        TeamView next_opp_team;
        if(next_team1.getBattleIdx() == myTeamIdx){
            next_my_team = next_team1;
            next_opp_team = next_team2;
           
        }else{
            next_my_team = next_team2;
            next_opp_team = next_team1;
        }

        double reward = 0.0;

        double cohp = 0.0;
        double nohp = 0.0;


        for(int i = 0; i<opp_team.size(); i++){
            PokemonView p = opp_team.getPokemonView(i);
            PokemonView pp = next_opp_team.getPokemonView(i);
            if(p != null){
                cohp += (double)p.getCurrentStat(Stat.HP); 
            }
            if(pp!=null){
                nohp += (double)pp.getCurrentStat(Stat.HP); 
            }
        }

        // double ihp =0.0;
        // for(int i = 0; i<6; i++){
        //     PokemonView p = opp_team.getPokemonView(i);
        //     //PokemonView pp = next_opp_team.getPokemonView(i);
        //     if(p != null){
        //         ihp += (double)p.getInitialStat(Stat.HP); 
        //     }
        //     // if(pp!=null){
        //     //     nohp += (double)pp.getCurrentStat(Stat.HP); 
        //     // }
        // }

        reward += (cohp - nohp);

        // Type moveType = action.getType();
        // PokemonView opp_pokemon_view = opp_team.getActivePokemonView();
        // Type opp_type1 = opp_pokemon_view.getCurrentType1();
        // Type opp_type2 = opp_pokemon_view.getCurrentType2();

        // boolean eff1 = moveType.isSuperEffective(moveType, opp_type1);

        // boolean eff2 = false;
        // if(opp_type2!=null){
        //     eff2 = moveType.isSuperEffective(moveType, opp_type2);
        // }

        // if(eff1 || eff2){
        //     reward += 100.0;
        // }
        

        return reward;


        // PokemonView my_pokemon_view = my_team.getActivePokemonView();
        // PokemonView opp_pokemon_view = opp_team.getActivePokemonView();
        //PokemonView next_myP   = next_my_team.getActivePokemonView();
        //PokemonView next_oppP  = next_opp_team.getActivePokemonView();

        //double s_myHpRatio  = 0.0;
        //double s_oppHpRatio = 0.0;
        //double n_myHpRatio  = 0.0;
        //double n_oppHpRatio = 0.0;

       
        //double s_myMaxHp  = my_pokemon_view.getInitialStat(Stat.HP);
        //double s_oppMaxHp = opp_pokemon_view.getInitialStat(Stat.HP);
        //double n_myMaxHp  = next_myP.getInitialStat(Stat.HP);
        //double n_oppMaxHp = next_oppP.getInitialStat(Stat.HP);

        // if (s_myMaxHp > 0) {
        //     s_myHpRatio = my_pokemon_view.getCurrentStat(Stat.HP) / s_myMaxHp;
        // }
        // if (s_oppMaxHp > 0) {
        //     s_oppHpRatio = opp_pokemon_view.getCurrentStat(Stat.HP) / s_oppMaxHp;
        // }
        // if (n_myMaxHp > 0) {
        //     n_myHpRatio = next_myP.getCurrentStat(Stat.HP) / n_myMaxHp;
        // }
        // if (n_oppMaxHp > 0) {
        //     n_oppHpRatio = next_oppP.getCurrentStat(Stat.HP) / n_oppMaxHp;
        // }

    
        //double myHpDiff  = s_myHpRatio  - n_myHpRatio;   
        //double oppHpDiff = s_oppHpRatio - n_oppHpRatio;  


        //reward += oppHpDiff * 10.0;
        //reward -= myHpDiff * 10.0;


        // Integer power = action.getPower();
        // if (power != null && power > 0) {
        //     reward += 1.0;
        // }

        // Type moveType = action.getType();

        // Type opp_type1 = opp_pokemon_view.getCurrentType1();
        // Type opp_type2 =  opp_pokemon_view.getCurrentType2();

        // boolean eff1 = moveType.isSuperEffective(moveType, opp_type1);

        // boolean eff2 = false;
        // if(opp_type2!=null){
        //     eff2 = moveType.isSuperEffective(moveType, opp_type2);
        // }

        // if(eff1 || eff2){
        //     reward += 3.0;
        // }
         
        // if (my_pokemon_view.getCurrentStat(Stat.HP) < 0.2 * my_pokemon_view.getInitialStat(Stat.HP)) {
        //     reward -= 2.0; 
        // }


        //  if (my_pokemon_view.getCurrentStat(Stat.HP) < 0.2 * my_pokemon_view.getInitialStat(Stat.HP)) {
        //     reward -= 2.0; 
        // }

        // if(count_faint(next_opp_team)>count_faint(opp_team)){
        //     reward +=40.0;
        // }else if(count_faint(next_my_team)>count_faint(my_team)){
        //     reward -=40.0;
        // }else{
        //     reward +=0.0;
        // }

        // boolean i_lost = has_lost(next_my_team);
        // if(i_lost == true){
        //     // reward-=200.0;
        //     // return reward;
        //     return -100.0;
        // }
        // boolean opp_lost = has_lost(next_opp_team);
        // if(opp_lost ==true){
        //     // reward +=200.0;
        //     // return reward ;
        //     return 100.0;
        // }


        // if (reward > getUpperBound()) {
        // reward = getUpperBound();
        // } else if (reward < getLowerBound()) {
        // reward = getLowerBound();
        // }

        
    }

    //   private boolean has_lost(TeamView team) {
    //     for (int i = 0; i < 6; i++) {
    //         PokemonView p = team.getPokemonView(i);
    //         if (p != null && p.getCurrentStat(Stat.HP) > 0) {
    //             return false; 
    //         }
    //     }
    //     return true;
    // }

    // private int count_faint(TeamView team){
    //     int count = 0;
    //     for(int i = 0; i<6; i++){
    //         PokemonView p =  team.getPokemonView(i);
    //         if(p!=null && p.hasFainted()){
    //             count+=1;
    //         }
    //     }
    //     return count;
    // }






}