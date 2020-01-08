"""CS 61A Presents The Game of Hog."""

from dice import six_sided, four_sided, make_test_dice
from ucb import main, trace, interact

GOAL_SCORE = 100  # The goal of Hog is to score 100 points.

######################
# Phase 1: Simulator #
######################


def roll_dice(num_rolls, dice=six_sided):
    """Simulate rolling the DICE exactly NUM_ROLLS > 0 times. Return the sum of
    the outcomes unless any of the outcomes is 1. In that case, return 1.

    num_rolls:  The number of dice rolls that will be made.
    dice:       A function that simulates a single dice roll outcome.
    """
    # These assert statements ensure that num_rolls is a positive integer.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls > 0, 'Must roll at least once.'
    # BEGIN PROBLEM 1
    summation = 0
    outcome = 0
    for i in range(num_rolls):
     rollOutcome = dice()
     if (rollOutcome == 1):
       outcome = 1
     else:
      summation += rollOutcome

    if outcome == 1:
        return 1
    return summation
    # END PROBLEM 1


def free_bacon(score):
    """Return the points scored from rolling 0 dice (Free Bacon).

    score:  The opponent's current score.
    """
    assert score < 100, 'The game should be over.'
    # BEGIN PROBLEM 2
    total = (2*(score//10))-score%10
    if total < 1:
        return 1
    return total
    # END PROBLEM 2


def take_turn(num_rolls, opponent_score, dice=six_sided):
    """Simulate a turn rolling NUM_ROLLS dice, which may be 0 (Free Bacon).
    Return the points scored for the turn by the current player.

    num_rolls:       The number of dice rolls that will be made.
    opponent_score:  The total score of the opponent.
    dice:            A function that simulates a single dice roll outcome.
    """
    # Leave these assert statements here; they help check for errors.
    assert type(num_rolls) == int, 'num_rolls must be an integer.'
    assert num_rolls >= 0, 'Cannot roll a negative number of dice in take_turn.'
    assert num_rolls <= 10, 'Cannot roll more than 10 dice.'
    assert opponent_score < 100, 'The game should be over.'
    # BEGIN PROBLEM 3
    if num_rolls == 0:
        score = free_bacon(opponent_score)
    else:
        score = roll_dice(num_rolls, dice)
    return score
    # END PROBLEM 3


def is_swap(player_score, opponent_score):
    """
    Return whether the current player's score has the same absolute
    difference between its last two digits as the opponent's score.
    """
    # BEGIN PROBLEM 4
    if player_score >= 100:
        if (abs((player_score% 100//10) - player_score % 10)) == (abs(opponent_score // 10 - opponent_score % 10)):
            return True
    elif opponent_score >= 100:
        if (abs(player_score // 10 - player_score % 10)) == (abs((opponent_score %100//10) - opponent_score % 10)):
            return True
    elif player_score >= 100 and opponent_score >= 100:
        if (abs(player_score // 100 - player_score % 10)) == (abs(opponent_score // 10 - opponent_score % 10)):
            return True
    elif (abs(player_score//10 - player_score%10)) == (abs(opponent_score//10 - opponent_score%10)):
        return True
    return False
    # END PROBLEM 4


def other(player):
    """Return the other player, for a player PLAYER numbered 0 or 1.

    >>> other(0)
    1
    >>> other(1)
    0
    """
    return 1 - player


def silence(score0, score1):
    """Announce nothing (see Phase 2)."""
    return silence


def play(strategy0, strategy1, score0=0, score1=0, dice=six_sided,
         goal=GOAL_SCORE, say=silence):
    """Simulate a game and return the final scores of both players, with Player
    0's score first, and Player 1's score second.

    A strategy is a function that takes two total scores as arguments (the
    current player's score, and the opponent's score), and returns a number of
    dice that the current player will roll this turn.

    strategy0:  The strategy function for Player 0, who plays first.
    strategy1:  The strategy function for Player 1, who plays second.
    score0:     Starting score for Player 0
    score1:     Starting score for Player 1
    dice:       A function of zero arguments that simulates a dice roll.
    goal:       The game ends and someone wins when this score is reached.
    say:        The commentary function to call at the end of the first turn.
    """
    player = 0  # Which player is about to take a turn, 0 (first) or 1 (second)
    # BEGIN PROBLEM 5

    while max(score0, score1) < goal:
        if player == 0:

            num_rolls = strategy0(score0, score1)
            score_past_0 = take_turn(num_rolls, score1, dice)
            score0 += score_past_0
            if score_past_0 == 0:
                score1 += num_rolls

            if is_swap(score0, score1):
                score0, score1 = score1, score0

        else:

            num_rolls_1 = strategy1(score1, score0)
            score_past_1 = take_turn(num_rolls_1, score0, dice)
            score1 += score_past_1
            if score_past_1 == 0:
                score0 += num_rolls_1


            if is_swap(score0, score1):
                x = score0
                score0, score1 = score1, x

        say = say(score0, score1)

        player = other(player)

    return score0, score1
    # END PROBLEM 5
    # (note that the indentation for the problem 6 prompt (***YOUR CODE HERE***) might be misleading)
    # BEGIN PROBLEM 6

    # END PROBLEM 6



#######################
# Phase 2: Commentary #
#######################


def say_scores(score0, score1):
    """A commentary function that announces the score for each player."""
    print("Player 0 now has", score0, "and Player 1 now has", score1)
    return say_scores

def announce_lead_changes(previous_leader=None):
    """Return a commentary function that announces lead changes.

    >>> f0 = announce_lead_changes()
    >>> f1 = f0(5, 0)
    Player 0 takes the lead by 5
    >>> f2 = f1(5, 12)
    Player 1 takes the lead by 7
    >>> f3 = f2(8, 12)
    >>> f4 = f3(8, 13)
    >>> f5 = f4(15, 13)
    Player 0 takes the lead by 2
    """
    def say(score0, score1):
        if score0 > score1:
            leader = 0
        elif score1 > score0:
            leader = 1
        else:
            leader = None
        if leader != None and leader != previous_leader:
            print('Player', leader, 'takes the lead by', abs(score0 - score1))
        return announce_lead_changes(leader)
    return say

def both(f, g):
    """Return a commentary function that says what f says, then what g says.

    >>> h0 = both(say_scores, announce_lead_changes())
    >>> h1 = h0(10, 0)
    Player 0 now has 10 and Player 1 now has 0
    Player 0 takes the lead by 10
    >>> h2 = h1(10, 6)
    Player 0 now has 10 and Player 1 now has 6
    >>> h3 = h2(6, 17) # Player 0 gets 7 points, then Swine Swap applies
    Player 0 now has 6 and Player 1 now has 17
    Player 1 takes the lead by 11
    """
    def say(score0, score1):
        return both(f(score0, score1), g(score0, score1))
    return say


def announce_highest(who, previous_high=0, previous_score=0):
    """Return a commentary function that announces when WHO's score
    increases by more than ever before in the game.

    >>> f0 = announce_highest(1) # Only announce Player 1 score gains
    >>> f1 = f0(12, 0)
    >>> f2 = f1(12, 11)
    11 point(s)! That's the biggest gain yet for Player 1
    >>> f3 = f2(20, 11)
    >>> f4 = f3(13, 20) # Player 1 gets 2 points, then Swine Swap applies
    >>> f5 = f4(20, 35) # Player 0 gets 22 points, then Swine Swap applies
    15 point(s)! That's the biggest gain yet for Player 1
    >>> f6 = f5(20, 47) # Player 1 gets 12 points; not enough for a new high
    >>> f7 = f6(21, 47)
    >>> f8 = f7(21, 77)
    30 point(s)! That's the biggest gain yet for Player 1
    >>> f9 = f8(77, 22) # Swap!
    >>> f10 = f9(33, 77) # Swap!
    55 point(s)! That's the biggest gain yet for Player 1
    """
    assert who == 0 or who == 1, 'The who argument should indicate a player.'
    # BEGIN PROBLEM 7
    sentence = "That's the biggest gain yet for Player"
    prev2= previous_score
    prev3 = previous_high
    def say_highest(score0, score1):
        # nonlocal previous_score, previous_high
        if who == 0:
            target = score0
        else:
            target = score1
        score_gap = target - prev2
        if score_gap <= prev3:
            return announce_highest(who, prev3, target)
        if score_gap > prev3:
            print(score_gap, "point(s)" * (1 - (1 // score_gap)) + '!', sentence, who)
            previous_high = score_gap

        return announce_highest(who, score_gap, target)

    return say_highest
    # END PROBLEM 7


#######################
# Phase 3: Strategies #
#######################


def always_roll(n):
    """Return a strategy that always rolls N dice.

    A strategy is a function that takes two total scores as arguments (the
    current player's score, and the opponent's score), and returns a number of
    dice that the current player will roll this turn.

    >>> strategy = always_roll(5)
    >>> strategy(0, 0)
    5
    >>> strategy(99, 99)
    5
    """
    def strategy(score, opponent_score):
        return n
    return strategy


def make_averaged(fn, num_samples=1000):
    """Return a function that returns the average value of FN when called.

    To implement this function, you will have to use *args syntax, a new Python
    feature introduced in this project.  See the project description.

    >>> dice = make_test_dice(4, 2, 5, 1)
    >>> averaged_dice = make_averaged(dice, 1000)
    >>> averaged_dice()
    3.0
    """
    # BEGIN PROBLEM 8
    def reter(*args):
        sum, i = 0, 0
        while i < num_samples:
            sum, i = sum + fn(*args), i + 1
        return sum / num_samples

    return reter
    # END PROBLEM 8


def max_scoring_num_rolls(dice=six_sided, num_samples=1000):
    """Return the number of dice (1 to 10) that gives the highest average turn
    score by calling roll_dice with the provided DICE over NUM_SAMPLES times.
    Assume that the dice always return positive outcomes.

    >>> dice = make_test_dice(1, 6)
    >>> max_scoring_num_rolls(dice)
    1
    """
    # BEGIN PROBLEM 9
    _max, number_of_dice, reter = 0, 10, 0
    avg = make_averaged(roll_dice)(number_of_dice, dice)
    while number_of_dice > 0:
        _max = max(_max, avg)
        if avg >= _max:
            reter = number_of_dice
        number_of_dice -= 1

    return reter
    # END PROBLEM 9


def winner(strategy0, strategy1):
    """Return 0 if strategy0 wins against strategy1, and 1 otherwise."""
    score0, score1 = play(strategy0, strategy1)
    if score0 > score1:
        return 0
    else:
        return 1


def average_win_rate(strategy, baseline=always_roll(4)):
    """Return the average win rate of STRATEGY against BASELINE. Averages the
    winrate when starting the game as player 0 and as player 1.
    """
    win_rate_as_player_0 = 1 - make_averaged(winner)(strategy, baseline)
    win_rate_as_player_1 = make_averaged(winner)(baseline, strategy)

    return (win_rate_as_player_0 + win_rate_as_player_1) / 2


def run_experiments():
    """Run a series of strategy experiments and report results."""
    if True:  # Change to False when done finding max_scoring_num_rolls
        six_sided_max = max_scoring_num_rolls(six_sided)
        print('Max scoring num rolls for six-sided dice:', six_sided_max)

    if False:  # Change to True to test always_roll(8)
        print('always_roll(8) win rate:', average_win_rate(always_roll(8)))

    if False:  # Change to True to test bacon_strategy
        print('bacon_strategy win rate:', average_win_rate(bacon_strategy))

    if False:  # Change to True to test swap_strategy
        print('swap_strategy win rate:', average_win_rate(swap_strategy))

    if False:  # Change to True to test final_strategy
        print('final_strategy win rate:', average_win_rate(final_strategy))

    "*** You may add additional experiments as you wish ***"


def bacon_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice if that gives at least MARGIN points, and
    rolls NUM_ROLLS otherwise.
    """
    # BEGIN PROBLEM 10

    if free_bacon(opponent_score) >= margin:
        return 0
    else:
        return num_rolls
    # END PROBLEM 10
''

def swap_strategy(score, opponent_score, margin=8, num_rolls=4):
    """This strategy rolls 0 dice when it triggers a beneficial swap. It also
    rolls 0 dice if it gives at least MARGIN points and does not trigger a
    non-beneficial swap. Otherwise, it rolls NUM_ROLLS.
    """
    # BEGIN PROBLEM 11
    bacon_points = free_bacon(opponent_score)

    if is_swap(score+bacon_points, opponent_score) and bacon_points+score < opponent_score:
        return 0
    elif not is_swap(bacon_points+score, opponent_score):
        return bacon_strategy(score, opponent_score, margin, num_rolls)
    else:
        return num_rolls

    # END PROBLEM 11

def expectation(n,score,opponent_score):
    if (score+opponent_score) % 7 ==0:
        return (3/4)**n*3*n - (1 - (3/4)**n)*n
    else:
        return (5/6)**n *4*n - (1-(5/6)**n)*n
def final_strategy(score, opponent_score):
    """Write a brief description of your final strategy.

    *** YOUR DESCRIPTION HERE ***
    """
    # BEGIN PROBLEM 12
    setup = False
    for n in range(1, 15):
        if is_swap(n + opponent_score, score) and opponent_score > score:
            setup_num_rolls = n
            setup = True

    free_p = 1 + max([int(i) for i in str(opponent_score)][-2::])

    SHOT = False
    p_num_rolls = []
    for n in range(1, 15):
        if expectation(n, score, opponent_score) > free_p:
            p_num_rolls.append(n)
            SHOT = True

    if free_p + score >= 100 and not is_swap(free_p + score, opponent_score):
        return 0
    elif setup:
        num_rolls = setup_num_rolls
        return num_rolls
    elif SHOT and score <= 100:
        if len(p_num_rolls) >= 2:
            return p_num_rolls[1]
        else:
            return p_num_rolls[0]

    elif is_swap(free_p + score, opponent_score) and free_p + score > opponent_score:
        return 5
    else:
        return 0
    # END PROBLEM 12


##########################
# Command Line Interface #
##########################

# NOTE: Functions in this section do not need to be changed. They use features
# of Python not yet covered in the course.


@main
def run(*args):
    """Read in the command-line argument and calls corresponding functions.

    This function uses Python syntax/techniques not yet covered in this course.
    """
    import argparse
    parser = argparse.ArgumentParser(description="Play Hog")
    parser.add_argument('--run_experiments', '-r', action='store_true',
                        help='Runs strategy experiments')

    args = parser.parse_args()

    if args.run_experiments:
        run_experiments()