package src.pas.pokemon.agents;


// SYSTEM IMPORTS
import net.sourceforge.argparse4j.inf.Namespace;

import java.util.ArrayList;
import java.util.List;

import edu.bu.pas.pokemon.agents.NeuralQAgent;
import edu.bu.pas.pokemon.agents.senses.SensorArray;
import edu.bu.pas.pokemon.core.Battle.BattleView;
import edu.bu.pas.pokemon.core.Move.MoveView;
import edu.bu.pas.pokemon.linalg.Matrix;
import edu.bu.pas.pokemon.nn.Model;
import edu.bu.pas.pokemon.nn.models.Sequential;
import edu.bu.pas.pokemon.nn.layers.Dense; // fully connected layer
import edu.bu.pas.pokemon.nn.layers.ReLU;  // some activations (below too)
import edu.bu.pas.pokemon.nn.layers.Tanh;
import edu.bu.pas.pokemon.nn.layers.Sigmoid;




import edu.bu.pas.pokemon.core.Team.TeamView;
import edu.bu.pas.pokemon.core.Pokemon.PokemonView;
import edu.bu.pas.pokemon.core.enums.Stat; // also needed for HP
import edu.bu.pas.pokemon.core.enums.NonVolatileStatus; // needed for getNonVolatileStatus
import src.pas.pokemon.rewards.CustomRewardFunction;
import edu.bu.pas.pokemon.nn.optimizers.AdamOptimizer;
// JAVA PROJECT IMPORTS
import src.pas.pokemon.senses.CustomSensorArray;


public class PolicyAgent
    extends NeuralQAgent
{

    public PolicyAgent()
    {
        super();
    }

    public void initializeSenses(Namespace args)
    {
        int myIdx = this.getMyTeamIdx();

        SensorArray senses = new CustomSensorArray(myIdx);
        this.setSensorArray(senses);
    }

    @Override
    public void initialize(Namespace args)
    {
       
        // make sure you call this, this will call your initModel() and set a field
        // AND if the command line argument "inFile" is present will attempt to set
        // your model with the contents of that file.
        super.initialize(args);

        // what senses will your neural network have?
        this.initializeSenses(args);

        // do what you want just don't expect custom command line options to be available
        // when I'm testing your code
    }

    @Override
    public Model initModel()
    {
        // TODO: create your neural network

        // currently this creates a one-hidden-layer network
        Sequential qFunction = new Sequential();
        qFunction.add(new Dense(8, 128));
        
        qFunction.add(new ReLU());
        qFunction.add(new Dense(128, 64));

        qFunction.add(new ReLU());
        qFunction.add(new Dense(64, 1));

        return qFunction;
    }

    @Override
    public Integer chooseNextPokemon(BattleView view)
    {
        // TODO: change this to something more intelligent!

        int bestIdx = 0;
        double bestScore = Double.NEGATIVE_INFINITY;

        // find a pokemon that is alive
        for(int idx = 0; idx < this.getMyTeamView(view).size(); ++idx)
        {
            if(!this.getMyTeamView(view).getPokemonView(idx).hasFainted()){

                //available move, getlevel,getNonVolatileStatusCounter(NonVolatileStatus stat)
                double score = computeSwitchScore(this.getMyTeamView(view).getPokemonView(idx));

                if (score > bestScore) {
                    bestScore = score;
                    bestIdx = idx;
                }
                
            }
        }
        return bestIdx;
    }

    private double computeSwitchScore(PokemonView p){
        double maxHp = p.getInitialStat(Stat.HP);
        double curHp = p.getCurrentStat(Stat.HP);
        double hpRatio;
        if(maxHp>0){
            hpRatio = (double) curHp/maxHp;
        }else{
            hpRatio = 0.0;
        }
    
        double levelScore = p.getLevel() / 100.0;

        double statusScore = 0.0;
        NonVolatileStatus status = p.getNonVolatileStatus();

        if (status == NonVolatileStatus.SLEEP || status == NonVolatileStatus.FREEZE) {
            int cnt = p.getNonVolatileStatusCounter(status);
            statusScore -= 0.5 * cnt;
        } else if (status == NonVolatileStatus.BURN
                || status == NonVolatileStatus.POISON
                || status == NonVolatileStatus.TOXIC) {
            statusScore -= 0.5;
        }    
        double totalScore = 1.5 * hpRatio + 1.0 * levelScore + 1.0 * statusScore;

        return totalScore;
    }
    
    private double epsilon      = 0.8;   
    private double minEpsilon   = 0.02;  
    private double epsilonDecay = 0.995; 

    private final java.util.Random rng = new java.util.Random();
    @Override
    public MoveView getMove(BattleView view)
    {
        // TODO: change this to include random exploration during training and maybe use the transition model to make
        // good predictions?
        // if you choose to use the transition model you might want to also override the makeGroundTruth(...) method
        // to not use temporal difference learning

        // currently always tries to argmax the learned model
        // this is not a good idea to always do when training. When playing evaluation games you *do* want to always
        // argmax your model, but when training our model may not know anything yet! So, its a good idea to sometime
        // during training choose *not* to argmax the model and instead choose something new at random.

        // HOW that randomness works and how often you do it are up to you, but it *will* affect the quality of your
        // learned model whether you do it or not!
        
        PokemonView active = this.getMyTeamView(view).getActivePokemonView();

        List<MoveView> legalMoves = new ArrayList<>();

        List<MoveView> availableMoves = active.getAvailableMoves();
        if (availableMoves != null) {
            for (MoveView mv : availableMoves) {
                if (mv.getPP() > 0) {     
                    legalMoves.add(mv);
                }
            }
        }

        if (legalMoves.isEmpty()) {
            return null;
        }

        if (rng.nextDouble() < epsilon) {
            int idx = rng.nextInt(legalMoves.size());
            return legalMoves.get(idx);
        }

        return this.argmax(view);
  
    }

    @Override
    public void afterGameEnds(BattleView view)
    {
        epsilon = Math.max(minEpsilon, epsilon * epsilonDecay);
       
    }

}