import { Component, Input, SimpleChanges } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { TweetService } from 'app/services/tweet.service';
import { Chart, registerables} from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-bar-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.css']
})
export class ChartComponent {
 
  
  
  public barChartLegend = false;

  data?: number[]
  tweets?: Tweet[]

  constructor(private tweetService: TweetService){
    
  }
  ngOnInit(): void{
    var myChart: any;
    this.tweetService.getMostLikedTweets(0, 6).subscribe(
      receivedTweets => {this.tweets = receivedTweets; this.defineChart(myChart)}
    )
  }

  defineChart(chart: any){

    const xValues: string[] = []
    const yValues: number[] = []

    this.tweets?.forEach(tweet => {
      xValues.push(`'${tweet.text}'\nby user: ${tweet.author.username}`)
      yValues.push(tweet.likes.length)
    });


    chart =
        new Chart("myChart", {
          type: "bar",
          data: {
            labels: xValues,
            datasets: [{
              label: "Top 5 most liked tweets",
              backgroundColor: ["red", "green","blue","orange","brown"],
              data: yValues
            }]
          },
          options: {
            scales: {
              y:{
                beginAtZero: true,
                ticks: {
                  precision: 0
                },
              },
              
            },
            plugins: {
              legend: {
                  labels: {
                    
                      font: {
                          size: 20
                      }
                  }
              }
          }
          }
        });
  }



  chartClicked(e: any): void {
    // Handle chart click event
  }

  chartHovered(e: any): void {
    // Handle chart hover event
  }
}
